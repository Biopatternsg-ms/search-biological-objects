package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BiologicalObjectService;
import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.application.services.ExpertObjectService;
import com.biopatternsg.application.services.PipelineService;
import com.biopatternsg.domain.models.pipeline_config.MinedObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@ApplicationScoped
public class PipelineServiceImpl implements PipelineService {

    private final ExpertObjectService expertObjectService;
    private final DiscoveryObjectService discoveryObjectService;
    private final BiologicalObjectService biologicalObjectService;
    private String pipelineId = null;
    private int levels = 0;

    @Override
    public void execute(PipelineConfig pipelineConfig) {

        this.pipelineId = pipelineConfig.getPipelineId();
        this.levels = pipelineConfig.getLevels();

        var expertObjectIds = expertObjectService.execute(pipelineConfig.getExpertObjects(), pipelineConfig.getPipelineId());

        Set<String> pipelineObjectIds = new HashSet<>(expertObjectIds);
        searchBiologicalObjectsLevels(pipelineObjectIds, expertObjectIds,2);
    }

    private void searchBiologicalObjectsLevels(Set<String> pipelineIds, List<String> parentIds, int level){

        Set<String> nextLevelObjects = new HashSet<>();
        if(level <= levels){
            //Explore biologicalObjectIds previous level
            parentIds.forEach(parentId ->{
                //Discover new biological objects
                var discoveredIds = discoveryObjectService.execute(parentId);
                //Build biologicalObjectConfig classes to save in collection
                var biologicalObjectConfigList = discoveryObjectService.buildBiologicalObjectConfig(discoveredIds);
                //Explore biologicalObjectConfig
                biologicalObjectConfigList.forEach(biologicalObjectConfig -> {
                    //Build minedConfig to save minedObject too
                    var newMinedObjectConfig = new MinedObjectConfig(level, pipelineId, parentId);
                    var biologicalObject = biologicalObjectService.execute(biologicalObjectConfig, newMinedObjectConfig);
                    // Add biological object to pipelineList
                    if(biologicalObject.getUniprotId() != null && pipelineIds.add(biologicalObject.getId())){
                        //Build objects config to next level
                        nextLevelObjects.add(biologicalObject.getId());
                    }
                });
            });
            //Build entry values to nextLevel
            searchBiologicalObjectsLevels(pipelineIds, nextLevelObjects.stream().toList(), level+1);
        }
    }
}
