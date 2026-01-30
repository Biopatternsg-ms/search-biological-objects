package com.biopatternsg.infrastructure.internal_services.impl;

import com.biopatternsg.infrastructure.clients.internal_clients.ConfigAndControlHttpClient;
import com.biopatternsg.infrastructure.dtos.PipelineStepRequest;
import com.biopatternsg.infrastructure.internal_services.QueryConfigAndControl;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class QueryConfigAndControlImp implements QueryConfigAndControl {

    private final ConfigAndControlHttpClient configAndControlHttpClient;

    public QueryConfigAndControlImp (@RestClient ConfigAndControlHttpClient configAndControlHttpClient) {
        this.configAndControlHttpClient = configAndControlHttpClient;
    }

    @Override
    @Retry
    public void updatePipelineStep(String pipelineId, PipelineStepRequest pipelineStep, String userId) {
        configAndControlHttpClient.updatePipelineStep(pipelineId, pipelineStep, userId);
    }
}
