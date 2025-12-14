package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;

import java.util.List;

public interface DiscoveryObjectStrategy {

    List<BiologicalObjectConfig> execute(String value);
}
