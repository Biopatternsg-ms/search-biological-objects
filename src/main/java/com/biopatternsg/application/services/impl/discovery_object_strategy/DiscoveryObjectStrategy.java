package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;

import java.util.List;
import java.util.Set;

public interface DiscoveryObjectStrategy {

    Set<String> execute(String value);
    List<BiologicalObjectConfig> buildBiologicalObjectConfig(List<String> ids);
}
