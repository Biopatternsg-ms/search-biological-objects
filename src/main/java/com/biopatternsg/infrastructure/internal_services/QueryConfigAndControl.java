package com.biopatternsg.infrastructure.internal_services;

import com.biopatternsg.infrastructure.dtos.PipelineStepRequest;

public interface QueryConfigAndControl {

    void updatePipelineStep(String pipelineId, PipelineStepRequest pipelineStep, String userId);
}
