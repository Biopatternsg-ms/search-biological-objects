package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DiscoveryObjectByPdb implements DiscoveryObjectStrategy{

    private final PdbRepository pdbRepository;
    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final UserRepository userRepository;

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
        var userId = userRepository.getUserId();
        biologicalObject.setUserId(userId);
        biologicalObject = biologicalObjectRepository.save(biologicalObject);
        return biologicalObject.getId();
    }
}
