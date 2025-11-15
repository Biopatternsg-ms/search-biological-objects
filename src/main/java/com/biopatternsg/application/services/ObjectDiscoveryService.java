package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.pipeline_config.DiscoveryObjectConfig;

public interface ObjectDiscoveryService {

    void execute (DiscoveryObjectConfig discoveryObjectConfig, String pipelineId);
}
