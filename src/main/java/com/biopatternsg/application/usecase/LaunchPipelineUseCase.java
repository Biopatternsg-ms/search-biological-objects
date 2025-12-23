package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.application.services.PipelineService;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class LaunchPipelineUseCase implements LaunchPipeline {

    private final DiscoveryObjectService discoveryObjectService;
    private final PipelineService pipelineService;

    @Override
    public void execute(PipelineConfig pipelineConfig) {

        long init = System.nanoTime();
        pipelineService.execute(pipelineConfig);
        long end = System.nanoTime();

        double milisegundos = (end-init) / 1000000.0;
        System.out.println("Tiempo de ejecución: " + milisegundos + " ms");
    }
}
