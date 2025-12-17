package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;

import java.util.List;

public interface DiscoveryObjectService {

    void execute (PipelineConfig pipelineConfig, String context);
    List<String> execute(String id);
}
