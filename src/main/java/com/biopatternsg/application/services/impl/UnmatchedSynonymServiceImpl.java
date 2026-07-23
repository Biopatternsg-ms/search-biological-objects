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

import com.biopatternsg.application.services.UnmatchedSynonymService;
import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.PipelineSynonym;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.UserRepository;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UnmatchedSynonymServiceImpl implements UnmatchedSynonymService {

    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final UserRepository userRepository;

    @Override
    public void resolve(List<PipelineSynonym> unmatchedSynonyms) {
        int total = unmatchedSynonyms.size();
        int processed = 0;

        for (PipelineSynonym synonym : unmatchedSynonyms) {
            processed++;
            resolveOne(synonym, processed, total);
        }
    }

    private void resolveOne(PipelineSynonym synonym, int processed, int total) {
        var config = BiologicalObjectConfig.builder()
                .symbol(synonym.name())
                .build();

        BiologicalObject biologicalObject = biologicalObjectSearch.request(config);
        biologicalObject.addSynonyms(synonym.synonyms());
        log.info("Biological object resolved for synonym name=[{}]: processed=[{}] of total=[{}]",
                synonym.name(), processed, total);
        if (biologicalObject.getId() == null) {
            biologicalObject.setUserId(userRepository.getUserId());
            biologicalObjectRepository.save(biologicalObject);
        } else {
            biologicalObjectRepository.update(biologicalObject);
        }
    }
}
