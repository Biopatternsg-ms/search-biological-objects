package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.application.services.PipelineService;
import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.MinedObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
@ApplicationScoped
public class DiscoveryObjectServiceImpl implements DiscoveryObjectService {

    private final PipelineService pipelineService;
    private final PdbRepository pdbRepository;
    private String pipelineId = null;
    private int searchLevel = 0;

    @Override
    public void execute(PipelineConfig pipelineConfig) {

        this.searchLevel = pipelineConfig.getLevels();
        this.pipelineId = pipelineConfig.getPipelineId();

        var pipelineBiologicalObjects = searchFirstLevel(pipelineConfig);
        Set<String> pipelineObjects = new HashSet<>(pipelineBiologicalObjects.keySet());
        searchBiologicalObjectsLevels(pipelineObjects, pipelineBiologicalObjects,2);
    }

    private MinedObjectConfig buildMinedObjectConfig(int level, String pipelineId, String parentId){

        return new MinedObjectConfig(level, pipelineId, parentId);
    }

    private Map<String, String> searchFirstLevel(PipelineConfig pipelineConfig){

        Map<String, String> firstLevel = new LinkedHashMap<>();

        MinedObjectConfig minedObjectConfig = buildMinedObjectConfig(1, this.pipelineId, null);
        pipelineConfig.getExpertObjects().forEach(expertObject -> {
            var biologicalObject = pipelineService.execute(expertObject, minedObjectConfig);
            firstLevel.putIfAbsent(biologicalObject.getId(), biologicalObject.getUniprotId());
        });

        return firstLevel;
    }

    private void searchBiologicalObjectsLevels(Set<String> pipelineBiologicalObjects, Map<String, String> uniprotParents, int levelPivot){

        Map<String, String> nextLevelObjects = new LinkedHashMap<>();
        if(levelPivot <= this.searchLevel){
            //Explore uniprotId parents
            uniprotParents.forEach((parentId, uniprotId) ->{
                //Search complexes
                List<Complex> complexes = pdbRepository.getComplexes(uniprotId);
                complexes.forEach(group -> {
                    group.getParticipants().forEach(participantId-> {
                        //Build biologicalConfig and minedConfig to save object
                        var newBiologicalObjectConfig = new BiologicalObjectConfig(participantId, null, null);
                        var newMinedObjectConfig = buildMinedObjectConfig(levelPivot, this.pipelineId, parentId);
                        var biologicalObject = pipelineService.execute(newBiologicalObjectConfig, newMinedObjectConfig);
                        //Add biological object to pipelineList
                        if(pipelineBiologicalObjects.add(biologicalObject.getId())){
                            //Build objects config to next level
                            nextLevelObjects.put(biologicalObject.getId(), biologicalObject.getUniprotId());
                        }
                    });
                });
            });

            //Build entry values to nextLevel
            searchBiologicalObjectsLevels(pipelineBiologicalObjects, nextLevelObjects, levelPivot+1);
        }
    }
}
