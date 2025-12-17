package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.application.services.ExpertObjectService;
import com.biopatternsg.application.services.PipelineService;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class PipelineServiceImpl implements PipelineService {

    private final MinedObjectRepository minedObjectRepository;
    private final ExpertObjectService expertObjectService;
    private final DiscoveryObjectService discoveryObjectService;

    public void execute(PipelineConfig pipelineConfig) {

        var firstLevelIds = firstLevel(pipelineConfig);
        findLevels(pipelineConfig.getPipelineId(), firstLevelIds, pipelineConfig.getLevels());
    }


    private List<String> firstLevel(PipelineConfig pipelineConfig){

        var biologicalObjectIds = expertObjectService.execute(pipelineConfig.getExpertObjects());

        log.info("firstLevel");

        //TODO Add transaction factors

        var minedObjects = newObjects(biologicalObjectIds, pipelineConfig.getPipelineId(), null, 1);

        minedObjectRepository.save(minedObjects);

        return biologicalObjectIds;
    }

    private void findLevels(String pipelineId, List<String> biologicalObjectIds, int levels){

        log.info("findLevels");

        for (int i = 2; i <= levels; i++) {

            List<MinedObject> newMinedObjects = new ArrayList<>();
            log.info("Level {}",i);

            for(String value: biologicalObjectIds){
                var newObjectIds = discoveryObjectService.execute(value);
                var newObjectsToSave = newObjects(newObjectIds, pipelineId, value, i);
                newMinedObjects.addAll(minedObjectRepository.save(newObjectsToSave));

                log.info("value: {}",value);
            }
            biologicalObjectIds = newMinedObjects.stream().map(MinedObject::getBiologicalObjectId).toList();
        }

    }

    private List<MinedObject> newObjects(List<String> newObjectIds, String pipelineId, String parentId, int level){

        var minedObjects = minedObjectRepository.find(newObjectIds, pipelineId)
                .stream()
                .map(MinedObject::getId)
                .toList();

        return newObjectIds
                .stream()
                .filter(newObject -> !minedObjects.contains(newObject))
                .map(id -> buildMinedObject(id, pipelineId, parentId, level))
                .toList();
    }


    private MinedObject buildMinedObject(String biologicalObjectId, String pipelineId, String parentId, int level){

        return MinedObject.builder()
                .pipelineId(pipelineId)
                .biologicalObjectId(biologicalObjectId)
                .biologicalObjectParentId(parentId)
                .level(level)
                .build();
    }

}
