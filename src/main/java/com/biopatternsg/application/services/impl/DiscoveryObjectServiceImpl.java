package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.application.services.impl.discovery_object_strategy.DiscoveryObjectContext;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class DiscoveryObjectServiceImpl implements DiscoveryObjectService {

    private final DiscoveryObjectContext discoveryObjectContext;

    @Override
    public List<String> execute(String id) {

        var discoveryContext = discoveryObjectContext.load("pdb");
        return discoveryContext.execute(id);
    }

    @Override
    public List<BiologicalObjectConfig> buildBiologicalObjectConfig(List<String> ids) {

        var discoveryContext = discoveryObjectContext.load("pdb");
        return discoveryContext.buildBiologicalObjectConfig(ids);
    }
}
