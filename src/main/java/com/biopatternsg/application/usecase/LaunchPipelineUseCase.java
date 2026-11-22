/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
