package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;

public interface DiscoveryObjectService {

    void execute (PipelineConfig pipelineConfig, String context);
}
