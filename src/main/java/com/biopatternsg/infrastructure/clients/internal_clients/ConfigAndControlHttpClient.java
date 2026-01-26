package com.biopatternsg.infrastructure.clients.internal_clients;

import com.biopatternsg.domain.enums.PipelineSteps;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "config-and-control-api")
public interface ConfigAndControlHttpClient {

    @PATCH
    @Path("/config-and-control/pipelines/update-step")
    Response updatePipelineStep(@RequestBody String pipelineId, PipelineSteps pipelineStep);
}
