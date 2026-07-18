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
package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DiscoveryObjectByPdb implements DiscoveryObjectStrategy{

    private final PdbRepository pdbRepository;
    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final UserRepository userRepository;

    private static final Pattern UNIPROT_ID =
            Pattern.compile("^([OPQJ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})$");

    @Override
    public List<String> execute(String value, Integer maxComplexes) {



        List<Complex> complexes = pdbRepository.getComplexes(value);
        if (maxComplexes != null && maxComplexes > 0) {
            complexes = complexes.stream().limit(maxComplexes).toList();
        }

        var uniprotIds = complexes.stream()
                .flatMap(complex -> complex.getParticipants().stream())
                .distinct()
                .filter(id -> UNIPROT_ID.matcher(id).matches()) //
                .toList();

        return findBiologicalObjects(uniprotIds);
    }

    private List<String> findBiologicalObjects(List<String> uniprotIds){

        List<String> biologicalObjectIds = new ArrayList<>();
        uniprotIds.forEach(id -> biologicalObjectIds.add(getBiologicalObjectId(id)));
        return biologicalObjectIds;
    }

    private String getBiologicalObjectId(String uniprotId){

        var biologicalObject = biologicalObjectSearch.request(BiologicalObjectConfig.builder().uniprotId(uniprotId).build());
        if(biologicalObject.getId() != null){
            return biologicalObject.getId();
        }
        var userId = userRepository.getUserId();
        biologicalObject.setUserId(userId);
        biologicalObject = biologicalObjectRepository.save(biologicalObject);
        return biologicalObject.getId();
    }
}
