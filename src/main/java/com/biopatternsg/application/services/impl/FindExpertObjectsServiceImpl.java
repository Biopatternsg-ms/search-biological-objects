package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.ExpertObjectService;
import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class FindExpertObjectsServiceImpl implements ExpertObjectService {

    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final MinedObjectRepository minedObjectRepository;

    public void execute(List<BiologicalObjectConfig> expertObjects, String pipelineId) {

        expertObjects.forEach(expertObjectsData -> {
            var biologicalObject = biologicalObjectSearch.request(expertObjectsData);
            save(biologicalObject, pipelineId);
        });
    }

    private void save(BiologicalObject biologicalObject, String pipelineId){

        if(biologicalObject.getId() == null){
            biologicalObject = biologicalObjectRepository.save(biologicalObject);
        }

        minedObjectRepository.save(buildMinedObject(biologicalObject, pipelineId));

        //TODO search ontologies
    }

    private MinedObject buildMinedObject(BiologicalObject biologicalObject, String pipelineId){

        return MinedObject.builder()
                .pipelineId(pipelineId)
                .biologicalObjectId(biologicalObject.getId())
                .level(1)
                .build();

    }

}
