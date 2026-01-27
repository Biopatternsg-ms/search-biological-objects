package com.biopatternsg.infrastructure.clients.internal_clients;

import com.biopatternsg.domain.enums.PipelineSteps;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-and-control-api")
public interface ConfigAndControlHttpClient {

    @PATCH
    @Path("/config-and-control/pipelines/update-step/{id}")
    void updatePipelineStep(@PathParam("id") String pipelineId, PipelineSteps pipelineStep, @HeaderParam("x-user-id") String userId);
}
