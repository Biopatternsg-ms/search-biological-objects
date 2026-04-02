package com.biopatternsg.infrastructure.clients.internal_clients;

import com.biopatternsg.infrastructure.dtos.PipelineStepInternalRequest;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-and-control-api")
public interface ConfigAndControlHttpClient {

    @PATCH
    @Retry
    @Path("/config-and-control/pipelines/update-step")
    void updatePipelineStep(PipelineStepInternalRequest updatePipelineStep, @HeaderParam("x-user-id") String userId);
}
