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
package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.DiscoveryObjectService;
import com.biopatternsg.application.services.impl.discovery_object_strategy.DiscoveryObjectContext;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class DiscoveryObjectServiceImpl implements DiscoveryObjectService {

    private final DiscoveryObjectContext discoveryObjectContext;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public void execute(PipelineConfig pipelineConfig, String context) {

        //var expertObjects = searchFirstLevel(pipelineConfig);
        //var discoveryObjects = discoveryObjectContext.load(context);
        //discoveryObjects.execute(expertObjects, pipelineConfig.getPipelineId(), pipelineConfig.getLevels());
    }

    @Override
    public List<String> execute(String id) {

        var biologicalObject = biologicalObjectRepository.findById(id);
        if(biologicalObject != null && biologicalObject.getUniprotId() != null){
            var discoveryObject = discoveryObjectContext.load("pdb");
            return discoveryObject.execute(biologicalObject.getUniprotId());
        }

        return List.of();
    }

}
