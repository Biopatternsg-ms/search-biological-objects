package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.*;
import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.out.repositories.ConfigAndControlRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
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

        firstLevel(pipelineConfig);
        findLevels(pipelineConfig.getPipelineId(), pipelineConfig.getLevels());
    }


    void firstLevel(PipelineConfig pipelineConfig) {

        List<String> biologicalObjectIdsFromTranscriptionFactors = new ArrayList<>();

        if (pipelineConfig.getTranscriptionFactorConfig() != null){
             biologicalObjectIdsFromTranscriptionFactors = transcriptionFactorsService.execute(pipelineConfig.getTranscriptionFactorConfig());
             configAndControlRepository.updatePipelineStep(pipelineConfig.getPipelineId(), PipelineSteps.TRANSCRIPTION_FACTOR);
        }

        var biologicalObjectIdsFromExpertObjects = expertObjectService.execute(pipelineConfig.getExpertObjects());

        var biologicalObjectIds = Stream.concat(
                biologicalObjectIdsFromTranscriptionFactors.stream(),
                biologicalObjectIdsFromExpertObjects.stream()
        ).toList();

        var minedObjects = newObjects(biologicalObjectIds, pipelineConfig.getPipelineId(), null, 1);

        minedObjectRepository.save(minedObjects);
        configAndControlRepository.updatePipelineStep(pipelineConfig.getPipelineId(), PipelineSteps.EXPERT_OBJECTS);
    }

    private void findLevels(String pipelineId, int levels) {

        for (int level = 2; level <= levels; level++) {

            var minedObjects = getObjectsLastLevel(level, pipelineId); // Se consultan los objetos del nivel Anterior

            for (var value : minedObjects) {
                var newObjectIds = discoveryObjectService.execute(value.getBiologicalObjectId());
                var newObjectsToSave = newObjects(newObjectIds, pipelineId, value.getBiologicalObjectId(), level);
                minedObjectRepository.save(newObjectsToSave);
            }

        }
        configAndControlRepository.updatePipelineStep(pipelineId, PipelineSteps.SEARCH_LEVELS);
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

}
