package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DiscoveryObjectByPdb implements DiscoveryObjectStrategy{

    private final PdbRepository pdbRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public  Set<String> execute(String biologicalObjectId) {

        Set<String> uniprotIds = new LinkedHashSet<>();
        var biologicalObject = biologicalObjectRepository.findById(biologicalObjectId);
        if(biologicalObject != null && biologicalObject.getUniprotId() != null){

            List<Complex> complexes = pdbRepository.getComplexes(biologicalObject.getUniprotId());
            complexes.forEach(group -> uniprotIds.addAll(group.getParticipants()));
        }

        return uniprotIds;
    }

    @Override
    public List<BiologicalObjectConfig> buildBiologicalObjectConfig(List<String> uniprotIds) {

        return uniprotIds.stream().map(uniprotId -> new BiologicalObjectConfig(uniprotId, null, null)).toList();
    }
}
