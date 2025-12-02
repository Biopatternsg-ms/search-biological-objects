package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.DiscoveryObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.ExpertObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@ApplicationScoped
public class DiscoveryObjectServiceImpl implements DiscoveryObjectService {

    private final MinedObjectRepository minedObjectRepository;
    private final FindExpertObjectsServiceImpl findExpertObjectsService;
    private final PdbRepository pdbRepository;

    @Override
    public void execute(DiscoveryObjectConfig discoveryObjectConfig, String pipelineId) {

        Set<String> pipelineObjects = new HashSet<>();
        var minedObjects = minedObjectRepository.findPipelineList(pipelineId);
        var pipeLineObjects = buildPipelineObjects(pipelineObjects, minedObjects);

        for(int level=1; level < discoveryObjectConfig.getLevels(); level++){

            minedObjects = minedObjectRepository.findLevelList(level, pipelineId);
            ExpertObjectConfig expertObjectConfig = new ExpertObjectConfig(level+1, pipelineId, null);

            minedObjects.forEach(object -> {
                if(!(object.getUniprotId() == null) && !object.getUniprotId().isBlank()){
                    List<Complex> complexes = pdbRepository.getComplexes(object.getUniprotId());
                    complexes.forEach(group -> {
                        var newObjects = group.getParticipants();
                        newObjects.forEach(newObjectId -> {
                            if(pipeLineObjects.add(newObjectId)){
                                BiologicalObjectConfig discoveredObject = new BiologicalObjectConfig(newObjectId, null, null);
                                expertObjectConfig.setIdFather(object.getUniprotId());
                                findExpertObjectsService.execute(discoveredObject,expertObjectConfig);
                            }
                        });
                    });
                }
            });
        }



    }

    private Set<String> buildPipelineObjects(Set<String> pipelineObjects, List<MinedObject> minedObjects){

        minedObjects.forEach(minedObject -> pipelineObjects.add(minedObject.getUniprotId()));
        return pipelineObjects;
    }
}
