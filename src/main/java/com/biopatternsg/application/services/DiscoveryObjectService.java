package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;

import java.util.List;

public interface DiscoveryObjectService {

    List<String> execute (String id);
    List<BiologicalObjectConfig> buildBiologicalObjectConfig (List<String> discoveredIds);
}
