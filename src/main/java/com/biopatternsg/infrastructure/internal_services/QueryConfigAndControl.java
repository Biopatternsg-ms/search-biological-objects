package com.biopatternsg.infrastructure.internal_services;

import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.domain.models.PipelineUpdate;
import jakarta.ws.rs.core.Response;

public interface QueryConfigAndControl {

    void updatePipelineStep(String pipelineId, PipelineSteps pipelineStep, String userId);
}
