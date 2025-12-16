package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class LaunchPipelineUseCase implements LaunchPipeline {

    private final DiscoveryObjectService discoveryObjectService;

    @Override
    public void execute(PipelineConfig pipelineConfig) {

        discoveryObjectService.execute(pipelineConfig, "pdb");
        //TODO call TranscriptionFactorService

        //TODO call ObjectDiscoveryService
    }
}
