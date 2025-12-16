package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.application.services.PipelineService;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.MinedObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DiscoveryObjectByPdb implements DiscoveryObjectStrategy{

    private final PipelineService pipelineService;
    private final PdbRepository pdbRepository;

    @Override
    public void execute(Set<BiologicalObject> expertObjects, String pipelineId, int searchLevel) {

        discoveryObjectsByLevel(expertObjects, pipelineId, searchLevel, 2);
    }

    private void discoveryObjectsByLevel(Set<BiologicalObject> pastLevelObjects, String pipelineId, int searchLevel, int levelPivot){

        Set<BiologicalObject> nextLevelObjects = new HashSet<>();
        if(levelPivot <= searchLevel){
            //Explore uniprotId parents
            pastLevelObjects.forEach(biologicalObject -> {
                if(biologicalObject.getUniprotId() != null){
                    var newObjects = buildNewObjectsList(biologicalObject.getUniprotId());
                    newObjects.forEach(biologicalObjectConfig -> {
                        //Build minedConfig to save object
                        var newMinedConfig = new MinedObjectConfig(levelPivot, pipelineId, biologicalObject.getUniprotId());
                        var newBiologicalObject = pipelineService.execute(biologicalObjectConfig, newMinedConfig);
                        if(newBiologicalObject.getUniprotId() != null){
                            //Build objects config to next level
                            nextLevelObjects.add(newBiologicalObject);
                        }
                    });
                }
            });
            //Build entry values to nextLevel
            discoveryObjectsByLevel(nextLevelObjects, pipelineId, searchLevel, levelPivot+1);
        }
    }

    private Set<BiologicalObjectConfig> buildNewObjectsList(String uniprotId){

        Set<BiologicalObjectConfig> response = new HashSet<>();
        List<Complex> complexes = pdbRepository.getComplexes(uniprotId);
        complexes.forEach(group -> {
            group.getParticipants().forEach(participantId-> {
                var newObjectConfig = new BiologicalObjectConfig(participantId, null, null);
                response.add(newObjectConfig);
            });
        });

        return response;
    }
}
