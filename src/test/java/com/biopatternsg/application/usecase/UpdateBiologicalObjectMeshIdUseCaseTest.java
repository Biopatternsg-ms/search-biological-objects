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

import com.biopatternsg.domain.exceptions.ApiException;
import com.biopatternsg.domain.exceptions.GeneralError;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBiologicalObjectMeshIdUseCaseTest {

    @Mock
    private BiologicalObjectRepository biologicalObjectRepository;

    @InjectMocks
    private UpdateBiologicalObjectMeshIdUseCase useCase;

    @Test
    @DisplayName("Should update meshId and persist when biological object exists")
    void shouldUpdateMeshIdWhenFound() {
        BiologicalObject bio = BiologicalObject.builder().id("bio-1").name("TP53").build();
        when(biologicalObjectRepository.findById("bio-1")).thenReturn(bio);

        useCase.execute("bio-1", "D016159");

        assertEquals("D016159", bio.getMeshId());
        verify(biologicalObjectRepository).update(bio);
    }

    @Test
    @DisplayName("Should throw ApiException NOT_FOUND when biological object does not exist")
    void shouldThrowNotFoundWhenMissing() {
        when(biologicalObjectRepository.findById("missing-id")).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class,
                () -> useCase.execute("missing-id", "D016159"));

        assertEquals(GeneralError.NOT_FOUND, exception.getGeneralError());
        verify(biologicalObjectRepository, never()).update(any());
    }
}
