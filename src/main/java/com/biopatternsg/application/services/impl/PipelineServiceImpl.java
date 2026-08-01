/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.*;
import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.domain.enums.Status;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.out.repositories.ConfigAndControlRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class PipelineServiceImpl implements PipelineService {

    private final MinedObjectRepository minedObjectRepository;
    private final ExpertObjectService expertObjectService;
    private final DiscoveryObjectService discoveryObjectService;
    private final TranscriptionFactorsService transcriptionFactorsService;
    private final ConfigAndControlRepository configAndControlRepository;

    public void execute(PipelineConfig pipelineConfig) {
        updatePipelineStep(pipelineConfig.getPipelineId(), PipelineSteps.EXPERT_OBJECTS, Status.IN_PROGRESS, null);
        updatePipelineStep(pipelineConfig.getPipelineId(), PipelineSteps.SEARCH_LEVELS, Status.IN_PROGRESS, null);
        firstLevel(pipelineConfig);
        findLevels(pipelineConfig);
    }


    void firstLevel(PipelineConfig pipelineConfig) {

        List<String> biologicalObjectIdsFromTranscriptionFactors = new ArrayList<>();

        if (pipelineConfig.getTranscriptionFactorConfig() != null){
            updatePipelineStep(pipelineConfig.getPipelineId(), PipelineSteps.TRANSCRIPTION_FACTOR, Status.IN_PROGRESS, null);
             biologicalObjectIdsFromTranscriptionFactors = transcriptionFactorsService.execute(pipelineConfig.getTranscriptionFactorConfig());
             int tfCount = biologicalObjectIdsFromTranscriptionFactors.size();
             updatePipelineStep(pipelineConfig.getPipelineId(), PipelineSteps.TRANSCRIPTION_FACTOR, Status.COMPLETED,
                     Map.of("transcriptionFactorsFound", String.valueOf(tfCount)));
        }

        var biologicalObjectIdsFromExpertObjects = expertObjectService.execute(pipelineConfig.getExpertObjects());
        int expertObjCount = biologicalObjectIdsFromExpertObjects.size();
        updatePipelineStep(pipelineConfig.getPipelineId(), PipelineSteps.EXPERT_OBJECTS, Status.COMPLETED,
                Map.of("expertObjectsValidated", String.valueOf(expertObjCount)));

        var biologicalObjectIds = Stream.concat(
                biologicalObjectIdsFromTranscriptionFactors.stream(),
                biologicalObjectIdsFromExpertObjects.stream()
        ).toList();

        var minedObjects = newObjects(biologicalObjectIds, pipelineConfig.getPipelineId(), null, 1);

        minedObjectRepository.save(minedObjects);
    }

    private void findLevels(PipelineConfig pipelineConfig) {
        String pipelineId = pipelineConfig.getPipelineId();
        int levels = pipelineConfig.getLevels();
        Integer maxComplexes = pipelineConfig.getMaxComplexes();

        log.info("start FindLevels pipelineId: {} y pipelineLevels: {} ",pipelineId, levels);
        for (int level = 2; level <= levels; level++) {

            var minedObjects = getObjectsLastLevel(level, pipelineId);
            log.info("Level {} y minedObjects: {}", level, minedObjects);
            for (var value : minedObjects) {

                log.info("Level {} y minedObject ID: {}", level, value.getBiologicalObjectId());
                var newObjectIds = discoveryObjectService.execute(value.getBiologicalObjectId(), maxComplexes);
                var newObjectsToSave = newObjects(newObjectIds, pipelineId, value.getBiologicalObjectId(), level);
                log.info("Level {} y newObjects: {}", level, newObjectsToSave);
                minedObjectRepository.save(newObjectsToSave);
            }

        }

        long totalMinedObjects = minedObjectRepository.findByPipelineId(pipelineId).size();
        updatePipelineStep(pipelineId, PipelineSteps.SEARCH_LEVELS, Status.COMPLETED,
                Map.of(
                        "totalMinedObjects", String.valueOf(totalMinedObjects),
                        "searchLevels", String.valueOf(levels)
                ));
    }

    private List<MinedObject> getObjectsLastLevel(int level, String pipelineId) {
        return minedObjectRepository.findByLevel(level - 1, pipelineId);
    }

    private List<MinedObject> newObjects(List<String> newObjectIds, String pipelineId, String parentId, int level) {

        var minedObjects = minedObjectRepository.find(newObjectIds, pipelineId)
                .stream()
                .map(MinedObject::getBiologicalObjectId)
                .toList();

        return newObjectIds
                .stream()
                .filter(newObject -> !minedObjects.contains(newObject))
                .map(id -> buildMinedObject(id, pipelineId, parentId, level))
                .toList();
    }


    private MinedObject buildMinedObject(String biologicalObjectId, String pipelineId, String parentId, int level) {

        return MinedObject.builder()
                .pipelineId(pipelineId)
                .biologicalObjectId(biologicalObjectId)
                .biologicalObjectParentId(parentId)
                .level(level)
                .build();
    }

    private void updatePipelineStep(String pipelineId, PipelineSteps step, Status status, Map<String, String> metrics) {
        configAndControlRepository.updatePipelineStep(pipelineId, step, status, metrics);
    }

}
