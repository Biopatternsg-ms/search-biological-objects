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

import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.domain.enums.Status;
import com.biopatternsg.domain.exceptions.ApiException;
import com.biopatternsg.domain.exceptions.GeneralError;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.PaginatedResult;
import com.biopatternsg.domain.models.PipelineSynonym;
import com.biopatternsg.application.services.UnmatchedSynonymService;

import com.biopatternsg.domain.port.out.external_repositories.PubmedIntegrationRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import com.biopatternsg.domain.port.out.repositories.ConfigAndControlRepository;
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGenerationUseCase implements UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration {

    private final MinedObjectRepository minedObjectRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final PubmedIntegrationRepository pubmedIntegrationRepository;
    private final UnmatchedSynonymService unmatchedSynonymService;
    private final ConfigAndControlRepository configAndControlRepository;
    private static final int PAGE_SIZE = 100;
    private static final int MAX_DEPENDENCY_RESOLUTION_PASSES = 50;

    @Override
    public void execute(String pipelineId) {
        try {
            log.info("Starting update of biological objects synonyms for pipelineId=[{}]", pipelineId);

            List<BiologicalObject> biologicalObjects = getBiologicalObjects(pipelineId);
            log.info("Retrieved [{}] biological objects for pipelineId=[{}] before fetching synonyms", biologicalObjects.size(), pipelineId);

            List<PipelineSynonym> unmatchedSynonyms = new ArrayList<>();
            List<PipelineSynonym> allSynonyms = new ArrayList<>();
            Set<BiologicalObject> dirtyObjects = new LinkedHashSet<>();
            int currentPage = 0;
            int totalPages = 1;

            Map<String, List<BiologicalObject>> synonymIndex = buildSynonymIndex(biologicalObjects);

            while (currentPage < totalPages) {
                PaginatedResult<PipelineSynonym> pageResult = pubmedIntegrationRepository.getSynonyms(pipelineId, currentPage, PAGE_SIZE);
                if (isPageEmpty(pageResult)) {
                    break;
                }

                processSynonymsBatch(pageResult.items(), synonymIndex, unmatchedSynonyms, dirtyObjects);

                allSynonyms.addAll(pageResult.items());
                totalPages = pageResult.totalPages();
                currentPage++;
            }

            log.info("Finished pagination pass. Successfully processed [{}] synonyms from pubmed-integration. [{}] items did not match any biological object in the first pass.",
                    allSynonyms.size(), unmatchedSynonyms.size());

            resolveDependencies(allSynonyms, biologicalObjects, dirtyObjects);

            if (!dirtyObjects.isEmpty()) {
                log.info("Persisting [{}] modified biological objects.", dirtyObjects.size());
                biologicalObjectRepository.updateAll(new ArrayList<>(dirtyObjects));
            }

            unmatchedSynonymService.resolve(unmatchedSynonyms);

            configAndControlRepository.updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.COMPLETED);
        } catch (Exception e) {
            log.error("Error updating biological objects synonyms for pipelineId=[{}]", pipelineId, e);
            configAndControlRepository.updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.FAILED);
            throw new ApiException(GeneralError.INTERNAL_SERVER_ERROR, 500, e.getMessage());
        }
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

    /**
     * Builds an inverted index mapping each lowercase synonym to the list of BiologicalObjects that contain it.
     * This enables O(1) lookups per synonym instead of O(M × S) linear scans.
     */
    Map<String, List<BiologicalObject>> buildSynonymIndex(List<BiologicalObject> biologicalObjects) {
        Map<String, List<BiologicalObject>> index = new HashMap<>();
        for (BiologicalObject bo : biologicalObjects) {
            for (String synonym : bo.getSynonyms()) {
                index.computeIfAbsent(synonym.toLowerCase(), k -> new ArrayList<>()).add(bo);
            }
        }
        return index;
    }

    /**
     * Returns the set of BiologicalObjects that share at least one synonym (case-insensitive)
     * with the given PipelineSynonym, using the prebuilt inverted index.
     */
    private Set<BiologicalObject> findMatchingObjects(PipelineSynonym pubSynonym, Map<String, List<BiologicalObject>> index) {
        Set<BiologicalObject> matches = new LinkedHashSet<>();
        if (pubSynonym.synonyms() == null) {
            return matches;
        }
        for (String syn : pubSynonym.synonyms()) {
            List<BiologicalObject> found = index.get(syn.toLowerCase());
            if (found != null) {
                matches.addAll(found);
            }
        }
        return matches;
    }

    private void processSynonymsBatch(List<PipelineSynonym> batch, Map<String, List<BiologicalObject>> synonymIndex,
                                       List<PipelineSynonym> unmatchedSynonyms, Set<BiologicalObject> dirtyObjects) {
        batch.forEach(pubSynonym -> processSingleSynonym(pubSynonym, synonymIndex, unmatchedSynonyms, dirtyObjects));
    }

    private void processSingleSynonym(PipelineSynonym pubSynonym, Map<String, List<BiologicalObject>> synonymIndex,
                                       List<PipelineSynonym> unmatchedSynonyms, Set<BiologicalObject> dirtyObjects) {
        Set<BiologicalObject> matches = findMatchingObjects(pubSynonym, synonymIndex);
        if (matches.isEmpty()) {
            unmatchedSynonyms.add(pubSynonym);
            return;
        }
        for (BiologicalObject bo : matches) {
            List<String> missingSynonyms = getMissingSynonyms(pubSynonym, bo);
            if (!missingSynonyms.isEmpty()) {
                log.info("Adding missing synonyms to biological object ID=[{}] (Name=[{}], Symbol=[{}]): {}",
                        bo.getId(), bo.getName(), bo.getSymbol(), missingSynonyms);
                bo.addSynonyms(missingSynonyms);
                updateSynonymIndex(synonymIndex, bo, missingSynonyms);
                dirtyObjects.add(bo);
            }
        }
    }

    /**
     * Resolves transitive synonym dependencies through iterative passes.
     * After the first pass adds new synonyms to biological objects, previously unmatched
     * PipelineSynonyms may now match. This method rebuilds the inverted index each iteration
     * and repeats until no new synonyms are discovered or MAX_DEPENDENCY_RESOLUTION_PASSES is reached.
     */
    private void resolveDependencies(List<PipelineSynonym> allSynonyms, List<BiologicalObject> biologicalObjects,
                                      Set<BiologicalObject> dirtyObjects) {
        if (allSynonyms.isEmpty()) {
            return;
        }

        log.info("Starting dependency resolution pass for [{}] synonyms...", allSynonyms.size());
        boolean matchFoundInIteration;
        int passCount = 0;

        do {
            passCount++;
            if (passCount > MAX_DEPENDENCY_RESOLUTION_PASSES) {
                log.warn("Dependency resolution exceeded max iterations [{}]. Breaking.", MAX_DEPENDENCY_RESOLUTION_PASSES);
                break;
            }

            matchFoundInIteration = false;
            log.info("Running dependency resolution iteration [{}]...", passCount);

            Map<String, List<BiologicalObject>> index = buildSynonymIndex(biologicalObjects);

            for (PipelineSynonym pubSynonym : allSynonyms) {
                Set<BiologicalObject> matches = findMatchingObjects(pubSynonym, index);
                for (BiologicalObject bo : matches) {
                    List<String> missingSynonyms = getMissingSynonyms(pubSynonym, bo);
                    if (!missingSynonyms.isEmpty()) {
                        log.info("Dependency resolved. Adding missing synonyms to biological object ID=[{}] (Name=[{}], Symbol=[{}]): {}",
                                bo.getId(), bo.getName(), bo.getSymbol(), missingSynonyms);
                        bo.addSynonyms(missingSynonyms);
                        dirtyObjects.add(bo);
                        matchFoundInIteration = true;
                    }
                }
            }
        } while (matchFoundInIteration);

        log.info("Dependency resolution finished. Completed in [{}] iterations.", passCount);
    }

    /**
     * Incrementally updates the inverted index with newly added synonyms for a biological object,
     * avoiding a full index rebuild during the first-pass processing.
     */
    private void updateSynonymIndex(Map<String, List<BiologicalObject>> index, BiologicalObject bo, List<String> newSynonyms) {
        for (String newSyn : newSynonyms) {
            index.computeIfAbsent(newSyn.toLowerCase(), k -> new ArrayList<>()).add(bo);
        }
    }

    /**
     * Returns the list of synonyms from the PipelineSynonym that are NOT already present
     * in the BiologicalObject (case-insensitive comparison via HashSet lookup in O(1)).
     */
    private List<String> getMissingSynonyms(PipelineSynonym pubSynonym, BiologicalObject bo) {
        if (pubSynonym.synonyms() == null) {
            return Collections.emptyList();
        }
        Set<String> boSynonymsLower = new HashSet<>();
        Collection<String> boSynonyms = bo.getSynonyms();
        if (boSynonyms != null) {
            for (String s : boSynonyms) {
                boSynonymsLower.add(s.toLowerCase());
            }
        }
        return pubSynonym.synonyms().stream()
                .filter(pubSyn -> !boSynonymsLower.contains(pubSyn.toLowerCase()))
                .toList();
    }
}
