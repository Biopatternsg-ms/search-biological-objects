package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BiologicalObjectService;
import com.biopatternsg.application.services.ExpertObjectService;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.MinedObjectConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@ApplicationScoped
public class ExpertObjectServiceImpl implements ExpertObjectService {

    private final BiologicalObjectService biologicalObjectService;

    @Override
    public List<String> execute(List<BiologicalObjectConfig> expertObjects, String pipelineId) {

        Set<String> firstLevel = new HashSet<>();

        MinedObjectConfig minedObjectConfig =  new MinedObjectConfig(1, pipelineId, null);
        expertObjects.forEach(expertObject -> {
            var biologicalObject = biologicalObjectService.execute(expertObject, minedObjectConfig);
            if(biologicalObject.getUniprotId() != null){
                firstLevel.add(biologicalObject.getId());
            }
        });

        return firstLevel.stream().toList();
    }
}
