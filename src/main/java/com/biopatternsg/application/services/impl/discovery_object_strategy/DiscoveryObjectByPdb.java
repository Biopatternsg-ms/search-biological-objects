package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.application.services.PipelineService;
import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DiscoveryObjectByPdb implements DiscoveryObjectStrategy{

    private final PipelineService pipelineService;
    private final PdbRepository pdbRepository;
    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public void execute(Set<BiologicalObject> expertObjects, String pipelineId, int searchLevel) {

        //discoveryObjectsByLevel(expertObjects, pipelineId, searchLevel, 2);
    }

    @Override
    public List<String> execute(String value) {

        var uniprotIds = pdbRepository.getComplexes(value).stream()
                .flatMap(complex -> complex.getParticipants().stream())
                .collect(Collectors.toSet())
                .stream()
                .toList();

        return findBiologicalObjects(uniprotIds);
    }

    private List<String> findBiologicalObjects(List<String> uniprotIds){

        List<String> biologicalObjectIds = new ArrayList<>();
        uniprotIds.forEach(id -> biologicalObjectIds.add(getBiologicalObjectId(id)));
        return biologicalObjectIds;
    }

    private String getBiologicalObjectId(String uniprotId){

        var biologicalObject = biologicalObjectSearch.request(BiologicalObjectConfig.builder().uniprotId(uniprotId).build());
        if(biologicalObject.getId() != null){
            return biologicalObject.getId();
        }
        biologicalObject = biologicalObjectRepository.save(biologicalObject);
        return biologicalObject.getId();
    }
}
