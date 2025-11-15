package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.ExpertObjectService;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class LaunchPipelineUseCase implements LaunchPipeline {

    private final ExpertObjectService expertObjectService;

    @Override
    public void execute(PipelineConfig pipelineConfig) {
        expertObjectService.execute(pipelineConfig.getExpertObjects(), pipelineConfig.getPipelineId());

        //TODO call TranscriptionFactorService

        //TODO call ObjectDiscoveryService
    }
}
