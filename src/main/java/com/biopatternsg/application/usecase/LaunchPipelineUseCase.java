package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.ExpertObjectService;
import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.domain.models.pipeline_config.DiscoveryObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.ExpertObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class LaunchPipelineUseCase implements LaunchPipeline {

    private final ExpertObjectService expertObjectService;
    private final DiscoveryObjectService discoveryObjectService;

    @Override
    public void execute(PipelineConfig pipelineConfig) {

        var discoveryConfig = DiscoveryObjectConfig.builder()
                .levels(3)
                .build();

        ExpertObjectConfig expertObjectConfig = new ExpertObjectConfig(1, pipelineConfig.getPipelineId(),null);
        pipelineConfig.getExpertObjects().forEach(expertObject ->
                expertObjectService.execute(expertObject, expertObjectConfig));

        discoveryObjectService.execute(discoveryConfig, pipelineConfig.getPipelineId());
        //TODO call TranscriptionFactorService

        //TODO call ObjectDiscoveryService
    }
}
