package com.biopatternsg.infrastructure.internal_services;

import com.biopatternsg.infrastructure.dtos.PipelineStepInternalRequest;

public interface QueryConfigAndControl {

    void updatePipelineStep(PipelineStepInternalRequest updatePipelineStep, String userId);
}
