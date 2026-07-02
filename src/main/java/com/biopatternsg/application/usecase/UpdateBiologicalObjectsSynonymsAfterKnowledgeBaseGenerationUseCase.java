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

import com.biopatternsg.domain.models.PaginatedResult;
import com.biopatternsg.domain.models.PipelineSynonym;
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration;
import com.biopatternsg.domain.port.out.external_repositories.PubmedIntegrationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGenerationUseCase implements UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration {

    private final PubmedIntegrationRepository pubmedIntegrationRepository;
    private static final int PAGE_SIZE = 20;

    @Override
    public List<PipelineSynonym> execute(String pipelineId) {
        log.info("Starting update of biological objects synonyms for pipelineId=[{}]", pipelineId);

        List<PipelineSynonym> allSynonyms = new ArrayList<>();
        int currentPage = 0;
        int totalPages = 1;

        while (currentPage < totalPages) {
            PaginatedResult<PipelineSynonym> pageResult = pubmedIntegrationRepository.getSynonyms(pipelineId, currentPage, PAGE_SIZE);
            if (pageResult == null || pageResult.items() == null || pageResult.items().isEmpty()) {
                break;
            }
            allSynonyms.addAll(pageResult.items());
            totalPages = pageResult.totalPages();
            currentPage++;
        }

        log.info("Successfully fetched [{}] synonyms for pipelineId=[{}]", allSynonyms.size(), pipelineId);
        return allSynonyms;
    }
}
