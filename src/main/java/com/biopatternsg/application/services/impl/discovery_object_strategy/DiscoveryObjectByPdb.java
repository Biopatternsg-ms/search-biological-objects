package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DiscoveryObjectByPdb implements DiscoveryObjectStrategy{

    private final PdbRepository pdbRepository;

    @Override
    public  List<BiologicalObjectConfig> execute(String uniprotId) {

        List<BiologicalObjectConfig> response = new ArrayList<>();
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
