package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.ExpertObjectService;
import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.ExpertObjectConfig;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class FindExpertObjectsServiceImpl implements ExpertObjectService {

    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final MinedObjectRepository minedObjectRepository;

    public void execute(BiologicalObjectConfig expertObject, ExpertObjectConfig expertObjectConfig) {

        var biologicalObject = biologicalObjectSearch.request(expertObject);
        save(biologicalObject, expertObjectConfig);
    }

    private void save(BiologicalObject biologicalObject, ExpertObjectConfig expertObjectConfig){

        if(biologicalObject.getId() == null){
            biologicalObject = biologicalObjectRepository.save(biologicalObject);
        }

        MinedObject minedObject = minedObjectRepository.find(biologicalObject.getId());
        if(minedObject == null){
            minedObjectRepository.save(buildMinedObject(biologicalObject, expertObjectConfig));
        }

        //TODO search ontologies
    }

    private MinedObject buildMinedObject(BiologicalObject biologicalObject, ExpertObjectConfig expertObjectConfig){

        return MinedObject.builder()
                .pipelineId(expertObjectConfig.getPipelineId())
                .userId(biologicalObject.getUserId())
                .biologicalObjectId(biologicalObject.getId())
                .uniprotId(biologicalObject.getUniprotId())
                .uniprotIdFather(expertObjectConfig.getIdFather())
                .level(expertObjectConfig.getLevel())
                .build();
    }

}
