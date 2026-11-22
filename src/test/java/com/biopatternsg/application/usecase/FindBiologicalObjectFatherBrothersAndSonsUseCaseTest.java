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
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindBiologicalObjectFatherBrothersAndSonsUseCaseTest {

    @Mock
    private MinedObjectRepository minedObjectRepository;

    @Mock
    private BiologicalObjectRepository biologicalObjectRepository;

    @InjectMocks
    private FindBiologicalObjectFatherBrothersAndSonsUseCase useCase;

    @Test
    @DisplayName("Should return empty list when mined object is not found")
    void shouldReturnEmptyListWhenMinedObjectNotFound() {
        when(minedObjectRepository.find("bio-1", "pipe-1")).thenReturn(null);

        List<BiologicalObject> result = useCase.execute("pipe-1", "bio-1");

        assertTrue(result.isEmpty());
        verify(biologicalObjectRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should handle root node with null parentId without NullPointerException")
    void shouldHandleRootNodeWithNullParentId() {
        MinedObject rootMined = MinedObject.builder()
                .id("m-1")
                .pipelineId("pipe-1")
                .biologicalObjectId("bio-1")
                .biologicalObjectParentId(null)
                .level(1)
                .build();
        BiologicalObject rootBio = BiologicalObject.builder().id("bio-1").name("TP53").build();
        MinedObject sonMined = MinedObject.builder().id("m-2").biologicalObjectId("bio-son").biologicalObjectParentId("bio-1").build();
        BiologicalObject sonBio = BiologicalObject.builder().id("bio-son").name("MDM2").build();

        when(minedObjectRepository.find("bio-1", "pipe-1")).thenReturn(rootMined);
        when(biologicalObjectRepository.findById("bio-1")).thenReturn(rootBio);
        when(minedObjectRepository.findByParentId("bio-1", "pipe-1")).thenReturn(List.of(sonMined));
        when(biologicalObjectRepository.findByIds(List.of("bio-son"))).thenReturn(List.of(sonBio));

        List<BiologicalObject> result = useCase.execute("pipe-1", "bio-1");

        assertEquals(2, result.size());
        assertTrue(result.contains(rootBio));
        assertTrue(result.contains(sonBio));
        verify(minedObjectRepository, never()).findByParentId(null, "pipe-1");
    }

    @Test
    @DisplayName("Should return current object, father, brothers, and sons for non-root node")
    void shouldReturnFullLineageForNonRootNode() {
        MinedObject currentMined = MinedObject.builder()
                .id("m-2")
                .pipelineId("pipe-1")
                .biologicalObjectId("bio-2")
                .biologicalObjectParentId("bio-father")
                .level(2)
                .build();
        MinedObject brotherMined = MinedObject.builder()
                .id("m-3")
                .pipelineId("pipe-1")
                .biologicalObjectId("bio-brother")
                .biologicalObjectParentId("bio-father")
                .level(2)
                .build();
        MinedObject sonMined = MinedObject.builder()
                .id("m-4")
                .pipelineId("pipe-1")
                .biologicalObjectId("bio-son")
                .biologicalObjectParentId("bio-2")
                .level(3)
                .build();

        BiologicalObject currentBio = BiologicalObject.builder().id("bio-2").name("Current").build();
        BiologicalObject fatherBio = BiologicalObject.builder().id("bio-father").name("Father").build();
        BiologicalObject brotherBio = BiologicalObject.builder().id("bio-brother").name("Brother").build();
        BiologicalObject sonBio = BiologicalObject.builder().id("bio-son").name("Son").build();

        when(minedObjectRepository.find("bio-2", "pipe-1")).thenReturn(currentMined);
        when(biologicalObjectRepository.findById("bio-2")).thenReturn(currentBio);
        when(biologicalObjectRepository.findById("bio-father")).thenReturn(fatherBio);
        when(minedObjectRepository.findByParentId("bio-father", "pipe-1")).thenReturn(List.of(currentMined, brotherMined));
        when(biologicalObjectRepository.findByIds(List.of("bio-brother"))).thenReturn(List.of(brotherBio));
        when(minedObjectRepository.findByParentId("bio-2", "pipe-1")).thenReturn(List.of(sonMined));
        when(biologicalObjectRepository.findByIds(List.of("bio-son"))).thenReturn(List.of(sonBio));

        List<BiologicalObject> result = useCase.execute("pipe-1", "bio-2");

        assertEquals(4, result.size());
        assertTrue(result.containsAll(List.of(currentBio, fatherBio, brotherBio, sonBio)));
    }
}
