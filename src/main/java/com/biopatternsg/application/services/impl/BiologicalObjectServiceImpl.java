package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BiologicalObjectService;
import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.MinedObjectConfig;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class BiologicalObjectServiceImpl implements BiologicalObjectService {

    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final MinedObjectRepository minedObjectRepository;

    @Override
    public BiologicalObject execute(BiologicalObjectConfig expertObject, MinedObjectConfig minedObjectConfig) {

        var biologicalObject = biologicalObjectSearch.request(expertObject);
        return save(biologicalObject, minedObjectConfig);
    }

    private BiologicalObject save(BiologicalObject biologicalObject, MinedObjectConfig minedObjectConfig){

        if(biologicalObject.getId() == null){
            biologicalObject = biologicalObjectRepository.save(biologicalObject);
        }

        MinedObject minedObject = minedObjectRepository.find(biologicalObject.getId(), minedObjectConfig.getPipelineId());
        if(minedObject == null){
            minedObjectRepository.save(buildMinedObject(biologicalObject, minedObjectConfig));
        }

        //TODO search ontologies

        return biologicalObject;
    }

    private MinedObject buildMinedObject(BiologicalObject biologicalObject, MinedObjectConfig minedObjectConfig){

        return MinedObject.builder()
                .pipelineId(minedObjectConfig.getPipelineId())
                .userId(biologicalObject.getUserId())
                .biologicalObjectId(biologicalObject.getId())
                .biologicalObjectParentId(minedObjectConfig.getParentId())
                .level(minedObjectConfig.getLevel())
                .build();
    }
}
