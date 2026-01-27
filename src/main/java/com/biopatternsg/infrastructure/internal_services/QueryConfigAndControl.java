package com.biopatternsg.infrastructure.internal_services;

import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.domain.models.PipelineUpdate;
import com.biopatternsg.infrastructure.dtos.PipelineStepRequest;
import jakarta.ws.rs.core.Response;

public interface QueryConfigAndControl {

    void updatePipelineStep(String pipelineId, PipelineStepRequest pipelineStep, String userId);
}
