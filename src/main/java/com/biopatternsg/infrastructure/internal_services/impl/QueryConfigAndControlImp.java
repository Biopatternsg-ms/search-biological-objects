package com.biopatternsg.infrastructure.internal_services.impl;

import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.infrastructure.clients.internal_clients.ConfigAndControlHttpClient;
import com.biopatternsg.domain.models.PipelineUpdate;
import com.biopatternsg.infrastructure.internal_services.QueryConfigAndControl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class QueryConfigAndControlImp implements QueryConfigAndControl {

    private final ConfigAndControlHttpClient configAndControlHttpClient;

    public QueryConfigAndControlImp (@RestClient ConfigAndControlHttpClient configAndControlHttpClient) {
        this.configAndControlHttpClient = configAndControlHttpClient;
    }

    @Override
    public Response updatePipelineStep(String pipelineId, PipelineSteps pipelineStep) {
        return configAndControlHttpClient.updatePipelineStep(pipelineId, pipelineStep);
    }
}
