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

import com.biopatternsg.application.services.UnmatchedSynonymService;
import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.domain.enums.Status;
import com.biopatternsg.domain.exceptions.ApiException;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.models.PaginatedResult;
import com.biopatternsg.domain.models.PipelineSynonym;
import com.biopatternsg.domain.port.out.external_repositories.PubmedIntegrationRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.ConfigAndControlRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGenerationUseCaseTest {

    @Mock
    private MinedObjectRepository minedObjectRepository;

    @Mock
    private BiologicalObjectRepository biologicalObjectRepository;

    @Mock
    private PubmedIntegrationRepository pubmedIntegrationRepository;

    @Mock
    private UnmatchedSynonymService unmatchedSynonymService;

    @Mock
    private ConfigAndControlRepository configAndControlRepository;

    @InjectMocks
    private UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGenerationUseCase useCase;

    // ──────────────────────────────────────────────────────────────────────────
    // Existing behavior tests (updated for batch persistence via updateAll)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should successfully update biological objects with matching synonyms and mark step completed")
    void shouldSuccessfullyUpdateSynonyms() {
        // Arrange
        String pipelineId = "pipeline-123";
        String bioObjId = "bio-obj-1";

        MinedObject minedObject = MinedObject.builder()
                .pipelineId(pipelineId)
                .biologicalObjectId(bioObjId)
                .build();

        Set<String> initialSynonyms = new HashSet<>(List.of("BRCA1"));
        BiologicalObject bioObj = BiologicalObject.builder()
                .id(bioObjId)
                .symbol("BRCA1")
                .synonyms(initialSynonyms)
                .build();

        PipelineSynonym pubSynonym = new PipelineSynonym("BRCA1", List.of("BRCA1", "IRIS"));
        PaginatedResult<PipelineSynonym> pageResult = new PaginatedResult<>(
                List.of(pubSynonym), 1, 1, 0, 100
        );

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenReturn(List.of(minedObject));
        when(biologicalObjectRepository.findByIds(List.of(bioObjId))).thenReturn(List.of(bioObj));
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 0, 100)).thenReturn(pageResult);

        // Act
        useCase.execute(pipelineId);

        // Assert — the new synonym "IRIS" was added to the biological object
        assertTrue(bioObj.getSynonyms().contains("IRIS"));

        // Verify batch persistence instead of individual updates
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BiologicalObject>> captor = ArgumentCaptor.forClass(List.class);
        verify(biologicalObjectRepository).updateAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertSame(bioObj, captor.getValue().get(0));

        // Individual update should NOT be called
        verify(biologicalObjectRepository, never()).update(any());

        // No unmatched synonyms
        verify(unmatchedSynonymService).resolve(Collections.emptyList());

        verify(configAndControlRepository).updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.COMPLETED);
    }

    @Test
    @DisplayName("Should pass unmatched synonyms to UnmatchedSynonymService and mark step completed")
    void shouldHandleUnmatchedSynonyms() {
        // Arrange
        String pipelineId = "pipeline-123";
        String bioObjId = "bio-obj-1";

        MinedObject minedObject = MinedObject.builder()
                .pipelineId(pipelineId)
                .biologicalObjectId(bioObjId)
                .build();

        BiologicalObject bioObj = BiologicalObject.builder()
                .id(bioObjId)
                .symbol("BRCA1")
                .synonyms(new HashSet<>(List.of("BRCA1")))
                .build();

        // "TP53" / "p53" does NOT match "BRCA1"
        PipelineSynonym pubSynonym = new PipelineSynonym("TP53", List.of("TP53", "p53"));
        PaginatedResult<PipelineSynonym> pageResult = new PaginatedResult<>(
                List.of(pubSynonym), 1, 1, 0, 100
        );

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenReturn(List.of(minedObject));
        when(biologicalObjectRepository.findByIds(List.of(bioObjId))).thenReturn(List.of(bioObj));
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 0, 100)).thenReturn(pageResult);

        // Act
        useCase.execute(pipelineId);

        // Assert — no modifications, so updateAll should NOT be called
        verify(biologicalObjectRepository, never()).updateAll(any());
        verify(biologicalObjectRepository, never()).update(any());

        // Verify the unmatched synonym was forwarded to UnmatchedSynonymService
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<PipelineSynonym>> captor = ArgumentCaptor.forClass(List.class);
        verify(unmatchedSynonymService).resolve(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("TP53", captor.getValue().get(0).name());

        verify(configAndControlRepository).updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.COMPLETED);
    }

    @Test
    @DisplayName("Should resolve transitive synonym matches through dependency resolution pass")
    void shouldResolveTransitiveDependencies() {
        // Arrange
        String pipelineId = "pipeline-123";
        String bioObjId = "bio-obj-1";

        MinedObject minedObject = MinedObject.builder()
                .pipelineId(pipelineId)
                .biologicalObjectId(bioObjId)
                .build();

        // BiologicalObject only has "A" initially
        BiologicalObject bioObj = BiologicalObject.builder()
                .id(bioObjId)
                .symbol("A")
                .synonyms(new HashSet<>(List.of("A")))
                .build();

        // Scenario:
        // pubSynonymB: name="B", synonyms=["B", "C"] — does NOT initially match bioObj (which only has "A")
        // pubSynonymA: name="A", synonyms=["A", "B"] — matches bioObj via "A", adds "B"
        // After first pass: bioObj has {"A", "B"}
        // Dependency resolution: pubSynonymB now matches via "B", adds "C"
        // Final: bioObj has {"A", "B", "C"}
        PipelineSynonym pubSynonymB = new PipelineSynonym("B", List.of("B", "C"));
        PipelineSynonym pubSynonymA = new PipelineSynonym("A", List.of("A", "B"));

        PaginatedResult<PipelineSynonym> pageResult = new PaginatedResult<>(
                List.of(pubSynonymB, pubSynonymA), 2, 1, 0, 100
        );

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenReturn(List.of(minedObject));
        when(biologicalObjectRepository.findByIds(List.of(bioObjId))).thenReturn(List.of(bioObj));
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 0, 100)).thenReturn(pageResult);

        // Act
        useCase.execute(pipelineId);

        // Assert — all three synonyms were resolved transitively
        assertTrue(bioObj.getSynonyms().contains("A"));
        assertTrue(bioObj.getSynonyms().contains("B"));
        assertTrue(bioObj.getSynonyms().contains("C"));

        // Batch persistence called once with the modified bioObj
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BiologicalObject>> updateCaptor = ArgumentCaptor.forClass(List.class);
        verify(biologicalObjectRepository).updateAll(updateCaptor.capture());
        assertTrue(updateCaptor.getValue().contains(bioObj));

        // pubSynonymB was unmatched in the first pass
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<PipelineSynonym>> unmatchedCaptor = ArgumentCaptor.forClass(List.class);
        verify(unmatchedSynonymService).resolve(unmatchedCaptor.capture());
        assertEquals(1, unmatchedCaptor.getValue().size());
        assertEquals("B", unmatchedCaptor.getValue().get(0).name());

        verify(configAndControlRepository).updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.COMPLETED);
    }

    @Test
    @DisplayName("Should update pipeline step to FAILED and throw ApiException when an exception occurs")
    void shouldHandleExceptionAndFailStep() {
        // Arrange
        String pipelineId = "pipeline-123";

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> useCase.execute(pipelineId));
        assertEquals(500, exception.getCustomCode());
        assertTrue(exception.getCustomMessage().contains("Database error"));

        verify(configAndControlRepository).updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.FAILED);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // New tests for performance improvements
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Inverted index: should match synonyms case-insensitively via the index")
    void shouldMatchSynonymsCaseInsensitivelyViaIndex() {
        // Arrange
        String pipelineId = "pipeline-123";
        String bioObjId = "bio-obj-1";

        MinedObject minedObject = MinedObject.builder()
                .pipelineId(pipelineId)
                .biologicalObjectId(bioObjId)
                .build();

        BiologicalObject bioObj = BiologicalObject.builder()
                .id(bioObjId)
                .symbol("EGFR")
                .synonyms(new HashSet<>(List.of("EGFR", "ErbB-1")))
                .build();

        // "erbb-1" (lowercase) should match "ErbB-1" in bioObj
        PipelineSynonym pubSynonym = new PipelineSynonym("EGFR", List.of("erbb-1", "HER1"));
        PaginatedResult<PipelineSynonym> pageResult = new PaginatedResult<>(
                List.of(pubSynonym), 1, 1, 0, 100
        );

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenReturn(List.of(minedObject));
        when(biologicalObjectRepository.findByIds(List.of(bioObjId))).thenReturn(List.of(bioObj));
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 0, 100)).thenReturn(pageResult);

        // Act
        useCase.execute(pipelineId);

        // Assert — "HER1" added via case-insensitive match on "erbb-1"/"ErbB-1"
        assertTrue(bioObj.getSynonyms().contains("HER1"));
        verify(biologicalObjectRepository).updateAll(any());
        verify(configAndControlRepository).updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.COMPLETED);
    }

    @Test
    @DisplayName("Batch persistence: should persist all modified objects in a single updateAll call")
    void shouldBatchPersistMultipleModifiedObjects() {
        // Arrange
        String pipelineId = "pipeline-123";

        MinedObject mined1 = MinedObject.builder().pipelineId(pipelineId).biologicalObjectId("bo-1").build();
        MinedObject mined2 = MinedObject.builder().pipelineId(pipelineId).biologicalObjectId("bo-2").build();

        BiologicalObject bioObj1 = BiologicalObject.builder()
                .id("bo-1").symbol("GENE1").synonyms(new HashSet<>(List.of("GENE1"))).build();
        BiologicalObject bioObj2 = BiologicalObject.builder()
                .id("bo-2").symbol("GENE2").synonyms(new HashSet<>(List.of("GENE2"))).build();

        PipelineSynonym syn1 = new PipelineSynonym("GENE1", List.of("GENE1", "ALIAS1"));
        PipelineSynonym syn2 = new PipelineSynonym("GENE2", List.of("GENE2", "ALIAS2"));
        PaginatedResult<PipelineSynonym> pageResult = new PaginatedResult<>(
                List.of(syn1, syn2), 2, 1, 0, 100
        );

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenReturn(List.of(mined1, mined2));
        when(biologicalObjectRepository.findByIds(List.of("bo-1", "bo-2"))).thenReturn(List.of(bioObj1, bioObj2));
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 0, 100)).thenReturn(pageResult);

        // Act
        useCase.execute(pipelineId);

        // Assert — both modified objects persisted in a SINGLE updateAll call
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BiologicalObject>> captor = ArgumentCaptor.forClass(List.class);
        verify(biologicalObjectRepository, times(1)).updateAll(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertTrue(captor.getValue().contains(bioObj1));
        assertTrue(captor.getValue().contains(bioObj2));

        // No individual update calls
        verify(biologicalObjectRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should handle empty result from pubmed-integration gracefully")
    void shouldHandleEmptyPubmedResult() {
        // Arrange
        String pipelineId = "pipeline-123";

        MinedObject minedObject = MinedObject.builder()
                .pipelineId(pipelineId)
                .biologicalObjectId("bo-1")
                .build();

        BiologicalObject bioObj = BiologicalObject.builder()
                .id("bo-1").symbol("BRCA1").synonyms(new HashSet<>(List.of("BRCA1"))).build();

        PaginatedResult<PipelineSynonym> emptyResult = new PaginatedResult<>(
                Collections.emptyList(), 0, 0, 0, 100
        );

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenReturn(List.of(minedObject));
        when(biologicalObjectRepository.findByIds(List.of("bo-1"))).thenReturn(List.of(bioObj));
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 0, 100)).thenReturn(emptyResult);

        // Act
        useCase.execute(pipelineId);

        // Assert — no modifications, no batch writes
        verify(biologicalObjectRepository, never()).updateAll(any());
        verify(biologicalObjectRepository, never()).update(any());
        verify(configAndControlRepository).updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.COMPLETED);
    }

    @Test
    @DisplayName("Inverted index: shared synonym should match all biological objects that contain it")
    void shouldMatchAllObjectsSharingASynonymViaIndex() {
        // Arrange
        String pipelineId = "pipeline-123";

        MinedObject mined1 = MinedObject.builder().pipelineId(pipelineId).biologicalObjectId("bo-1").build();
        MinedObject mined2 = MinedObject.builder().pipelineId(pipelineId).biologicalObjectId("bo-2").build();

        // Both BOs share the synonym "ALIAS_SHARED"
        BiologicalObject bo1 = BiologicalObject.builder()
                .id("bo-1").symbol("GENE1").synonyms(new HashSet<>(List.of("GENE1", "ALIAS_SHARED"))).build();
        BiologicalObject bo2 = BiologicalObject.builder()
                .id("bo-2").symbol("GENE2").synonyms(new HashSet<>(List.of("GENE2", "ALIAS_SHARED"))).build();

        // A PipelineSynonym that matches via "ALIAS_SHARED" should enrich BOTH objects
        PipelineSynonym pubSynonym = new PipelineSynonym("SHARED", List.of("ALIAS_SHARED", "NEW_SYN"));
        PaginatedResult<PipelineSynonym> pageResult = new PaginatedResult<>(
                List.of(pubSynonym), 1, 1, 0, 100
        );

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenReturn(List.of(mined1, mined2));
        when(biologicalObjectRepository.findByIds(List.of("bo-1", "bo-2"))).thenReturn(List.of(bo1, bo2));
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 0, 100)).thenReturn(pageResult);

        // Act
        useCase.execute(pipelineId);

        // Assert — both biological objects received "NEW_SYN" via the shared synonym
        assertTrue(bo1.getSynonyms().contains("NEW_SYN"));
        assertTrue(bo2.getSynonyms().contains("NEW_SYN"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BiologicalObject>> captor = ArgumentCaptor.forClass(List.class);
        verify(biologicalObjectRepository).updateAll(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertTrue(captor.getValue().contains(bo1));
        assertTrue(captor.getValue().contains(bo2));
    }

    @Test
    @DisplayName("Should paginate correctly across multiple pages of synonyms")
    void shouldPaginateCorrectlyAcrossMultiplePages() {
        // Arrange
        String pipelineId = "pipeline-123";

        MinedObject minedObject = MinedObject.builder()
                .pipelineId(pipelineId).biologicalObjectId("bo-1").build();

        BiologicalObject bioObj = BiologicalObject.builder()
                .id("bo-1").symbol("BRCA1").synonyms(new HashSet<>(List.of("BRCA1"))).build();

        PipelineSynonym syn1 = new PipelineSynonym("BRCA1", List.of("BRCA1", "ALIAS_PAGE1"));
        PipelineSynonym syn2 = new PipelineSynonym("BRCA1", List.of("BRCA1", "ALIAS_PAGE2"));

        PaginatedResult<PipelineSynonym> page0 = new PaginatedResult<>(List.of(syn1), 2, 2, 0, 100);
        PaginatedResult<PipelineSynonym> page1 = new PaginatedResult<>(List.of(syn2), 2, 2, 1, 100);

        when(minedObjectRepository.findByPipelineId(pipelineId)).thenReturn(List.of(minedObject));
        when(biologicalObjectRepository.findByIds(List.of("bo-1"))).thenReturn(List.of(bioObj));
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 0, 100)).thenReturn(page0);
        when(pubmedIntegrationRepository.getSynonyms(pipelineId, 1, 100)).thenReturn(page1);

        // Act
        useCase.execute(pipelineId);

        // Assert — synonyms from BOTH pages were added
        assertTrue(bioObj.getSynonyms().contains("ALIAS_PAGE1"));
        assertTrue(bioObj.getSynonyms().contains("ALIAS_PAGE2"));

        // Both pages were requested
        verify(pubmedIntegrationRepository).getSynonyms(pipelineId, 0, 100);
        verify(pubmedIntegrationRepository).getSynonyms(pipelineId, 1, 100);

        verify(biologicalObjectRepository).updateAll(any());
        verify(configAndControlRepository).updatePipelineStep(pipelineId, PipelineSteps.UPDATE_SYNONYMS, Status.COMPLETED);
    }
}
