package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.application.services.impl.discovery_object_strategy.DiscoveryObjectContext;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class DiscoveryObjectServiceImpl implements DiscoveryObjectService {

    private final DiscoveryObjectContext discoveryObjectContext;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public void execute(PipelineConfig pipelineConfig, String context) {

        //var expertObjects = searchFirstLevel(pipelineConfig);
        //var discoveryObjects = discoveryObjectContext.load(context);
        //discoveryObjects.execute(expertObjects, pipelineConfig.getPipelineId(), pipelineConfig.getLevels());
    }

    @Override
    public List<String> execute(String id) {

        var biologicalObject = biologicalObjectRepository.findById(id);
        if(biologicalObject != null && biologicalObject.getUniprotId() != null){
            var discoveryObject = discoveryObjectContext.load("pdb");
            return discoveryObject.execute(biologicalObject.getUniprotId());
        }

        return List.of();
    }

}
