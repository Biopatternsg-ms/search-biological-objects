package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.application.services.PipelineService;
import com.biopatternsg.application.services.impl.discovery_object_strategy.DiscoveryObjectContext;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.MinedObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
@ApplicationScoped
public class DiscoveryObjectServiceImpl implements DiscoveryObjectService {

    private final PipelineService pipelineService;
    private final DiscoveryObjectContext discoveryObjectContext;

    @Override
    public void execute(PipelineConfig pipelineConfig, String context) {

        var expertObjects = searchFirstLevel(pipelineConfig);
        var discoveryObjects = discoveryObjectContext.load(context);
        discoveryObjects.execute(expertObjects, pipelineConfig.getPipelineId(), pipelineConfig.getLevels());
    }

    private Set<BiologicalObject> searchFirstLevel(PipelineConfig pipelineConfig){

        Set<BiologicalObject> firstLevel = new HashSet<>();
        var minedObjectConfig = new MinedObjectConfig(1, pipelineConfig.getPipelineId(), null);
        pipelineConfig.getExpertObjects().forEach(expertObject -> {
            var biologicalObject = pipelineService.execute(expertObject, minedObjectConfig);
            if(biologicalObject.getUniprotId() != null){
                firstLevel.add(biologicalObject);
            }
        });

        return firstLevel;
    }
}
