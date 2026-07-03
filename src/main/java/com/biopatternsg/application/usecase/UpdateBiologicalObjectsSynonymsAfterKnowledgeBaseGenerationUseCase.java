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

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.PaginatedResult;
import com.biopatternsg.domain.models.PipelineSynonym;
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration;
import com.biopatternsg.domain.port.out.external_repositories.PubmedIntegrationRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGenerationUseCase implements UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration {

    private final MinedObjectRepository minedObjectRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final PubmedIntegrationRepository pubmedIntegrationRepository;
    private static final int PAGE_SIZE = 100;

    @Override
    public List<PipelineSynonym> execute(String pipelineId) {
        log.info("Starting update of biological objects synonyms for pipelineId=[{}]", pipelineId);

        List<BiologicalObject> biologicalObjects = getBiologicalObjects(pipelineId);
        log.info("Retrieved [{}] biological objects for pipelineId=[{}] before fetching synonyms", biologicalObjects.size(), pipelineId);

        List<PipelineSynonym> unmatchedSynonyms = new ArrayList<>();
        List<PipelineSynonym> allSynonyms = new ArrayList<>();
        int currentPage = 0;
        int totalPages = 1;

        while (currentPage < totalPages) {
            PaginatedResult<PipelineSynonym> pageResult = pubmedIntegrationRepository.getSynonyms(pipelineId, currentPage, PAGE_SIZE);
            if (isPageEmpty(pageResult)) {
                break;
            }

            processSynonymsBatch(pageResult.items(), biologicalObjects, unmatchedSynonyms);
            
            allSynonyms.addAll(pageResult.items());
            totalPages = pageResult.totalPages();
            currentPage++;
        }

        log.info("Finished matching synonyms. Successfully processed [{}] synonyms from pubmed-integration. [{}] items did not match any biological object.",
                allSynonyms.size(), unmatchedSynonyms.size());
        return allSynonyms;
    }

    private List<BiologicalObject> getBiologicalObjects(String pipelineId) {
        List<MinedObject> minedObjects = minedObjectRepository.findByPipelineId(pipelineId);
        List<String> biologicalObjectIds = minedObjects.stream()
                .map(MinedObject::getBiologicalObjectId)
                .toList();
        return biologicalObjectRepository.findByIds(biologicalObjectIds);
    }

    private boolean isPageEmpty(PaginatedResult<PipelineSynonym> pageResult) {
        return pageResult == null || pageResult.items() == null || pageResult.items().isEmpty();
    }

    private void processSynonymsBatch(List<PipelineSynonym> batch, List<BiologicalObject> biologicalObjects, List<PipelineSynonym> unmatchedSynonyms) {
        batch.forEach(pubSynonym -> processSingleSynonym(pubSynonym, biologicalObjects, unmatchedSynonyms));
    }

    private void processSingleSynonym(PipelineSynonym pubSynonym, List<BiologicalObject> biologicalObjects, List<PipelineSynonym> unmatchedSynonyms) {
        boolean matched = false;
        for (BiologicalObject bo : biologicalObjects) {
            if (anySynonymMatches(pubSynonym.synonyms(), bo.getSynonyms())) {
                matched = true;
                
                List<String> missingSynonyms = getMissingSynonyms(pubSynonym, bo);
                if (!missingSynonyms.isEmpty()) {
                    log.info("Adding missing synonyms to biological object ID=[{}] (Name=[{}]): {}", 
                            bo.getId(), bo.getName(), missingSynonyms);
                    bo.getSynonyms().addAll(missingSynonyms);
                    biologicalObjectRepository.update(bo);
                }
            }
        }
        if (!matched) {
            unmatchedSynonyms.add(pubSynonym);
        }
    }

    private boolean anySynonymMatches(List<String> pubSynonyms, Collection<String> boSynonyms) {
        if (pubSynonyms == null || boSynonyms == null) {
            return false;
        }
        return pubSynonyms.stream()
                .anyMatch(pubSyn -> boSynonyms.stream().anyMatch(pubSyn::equalsIgnoreCase));
    }

    private List<String> getMissingSynonyms(PipelineSynonym pubSynonym, BiologicalObject bo) {
        if (pubSynonym.synonyms() == null) {
            return Collections.emptyList();
        }
        Collection<String> boSynonyms = bo.getSynonyms() != null ? bo.getSynonyms() : Collections.emptyList();
        return pubSynonym.synonyms().stream()
                .filter(pubSyn -> boSynonyms.stream().noneMatch(pubSyn::equalsIgnoreCase))
                .toList();
    }
}
