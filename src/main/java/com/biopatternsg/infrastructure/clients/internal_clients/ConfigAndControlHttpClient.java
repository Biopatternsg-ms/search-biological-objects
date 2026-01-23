package com.biopatternsg.infrastructure.clients.internal_clients;

import com.biopatternsg.domain.models.PipelineUpdate;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "config-and-control-api")
public interface ConfigAndControlHttpClient {

    @POST
    @Path("/gene-ontology/build-tree")
    Response updatePipelineStatus(PipelineUpdate pipelineUpdate);
}
