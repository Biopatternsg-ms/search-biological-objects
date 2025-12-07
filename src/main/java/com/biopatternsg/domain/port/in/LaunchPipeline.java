package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;

public interface LaunchPipeline {

    void execute(PipelineConfig pipelineConfig);
}
