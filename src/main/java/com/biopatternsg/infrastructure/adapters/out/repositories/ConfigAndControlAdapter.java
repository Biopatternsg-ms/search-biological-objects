package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.domain.port.out.repositories.ConfigAndControlRepository;
import com.biopatternsg.infrastructure.dtos.PipelineStepInternalRequest;
import com.biopatternsg.infrastructure.internal_services.QueryConfigAndControl;
import com.biopatternsg.infrastructure.session.SessionUtil;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ConfigAndControlAdapter implements ConfigAndControlRepository {

    private final SessionUtil sessionUtil;
    private final QueryConfigAndControl queryConfigAndControl;

    @Override
    public void updatePipelineStep(String pipelineId, PipelineSteps pipelineStep) {

        var pipelineStepRequest = updatePipelineStepBuild(pipelineId, pipelineStep);
        queryConfigAndControl.updatePipelineStep(pipelineStepRequest, sessionUtil.getUserId());
    }

    private PipelineStepInternalRequest updatePipelineStepBuild(String pipelineId, PipelineSteps pipelineStep){

        return PipelineStepInternalRequest.builder()
                .id(pipelineId)
                .step(pipelineStep)
                .build();
    }
}
