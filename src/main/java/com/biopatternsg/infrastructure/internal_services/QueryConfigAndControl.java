package com.biopatternsg.infrastructure.internal_services;

import com.biopatternsg.domain.models.PipelineUpdate;
import jakarta.ws.rs.core.Response;

public interface QueryConfigAndControl {

    Response updatePipelineStatus(PipelineUpdate pipelineUpdate);
}
