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

import com.biopatternsg.application.services.ExpertObjectService;
import com.biopatternsg.application.services.OntologiesService;
import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class ExpertObjectServicesImpl implements ExpertObjectService {

    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final OntologiesService ontologiesService;
    private final UserRepository userRepository;

    @Override
    public List<String> execute(List<BiologicalObjectConfig> expertObjects) {

        List<String> response = new ArrayList<>();

        var userId = userRepository.getUserId();

        expertObjects.forEach(expertObject -> {
            var biologicalObject = biologicalObjectSearch.request(expertObject);
            if(biologicalObject.getId() == null){
                biologicalObject.setUserId(userId);
                biologicalObject = biologicalObjectRepository.save(biologicalObject);
                ontologiesService.buildGeneOntologyTree(biologicalObject.getGeneOntology());
                ontologiesService.buildMeshOntologyTree(biologicalObject);
            }

            response.add(biologicalObject.getId());
        });

        return response;
    }
}
