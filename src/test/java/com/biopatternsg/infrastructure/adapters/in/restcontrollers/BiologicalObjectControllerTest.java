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
package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.exceptions.ApiException;
import com.biopatternsg.domain.exceptions.GeneralError;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObjectFatherBrothersAndSons;
import com.biopatternsg.domain.port.in.FindBiologicalObjectsByPipelineAndLevel;
import com.biopatternsg.domain.port.in.FindBiologicalObjectsBySynonym;
import com.biopatternsg.domain.port.in.GetExpertObjectsByPipelineAndLevel;
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectMeshId;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDetailDTO;
import com.biopatternsg.infrastructure.adapters.dtos.UpdateMeshIdRequest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiologicalObjectControllerTest {

    @Mock
    private FindBiologicalObject findBiologicalObject;

    @Mock
    private UpdateBiologicalObjectMeshId updateBiologicalObjectMeshId;

    @Mock
    private FindBiologicalObjectsByPipelineAndLevel findBiologicalObjectsByPipelineAndLevel;

    @Mock
    private FindBiologicalObjectFatherBrothersAndSons findBiologicalObjectFatherBrothersAndSons;

    @Mock
    private FindBiologicalObjectsBySynonym findBiologicalObjectsBySynonym;

    @Mock
    private GetExpertObjectsByPipelineAndLevel getExpertObjectsByPipelineAndLevel;

    @InjectMocks
    private BiologicalObjectController controller;

    @Test
    @DisplayName("Should return 200 OK when updateMeshId succeeds")
    void shouldReturnOkWhenUpdateMeshIdSucceeds() {
        UpdateMeshIdRequest request = new UpdateMeshIdRequest("bio-1", "D016159");

        try (Response response = controller.updateMeshId(request)) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            verify(updateBiologicalObjectMeshId).execute("bio-1", "D016159");
        }
    }

    @Test
    @DisplayName("Should propagate ApiException when updateMeshId throws NOT_FOUND")
    void shouldPropagateApiExceptionOnMissingEntity() {
        UpdateMeshIdRequest request = new UpdateMeshIdRequest("missing-id", "D016159");
        doThrow(new ApiException(GeneralError.NOT_FOUND, GeneralError.NOT_FOUND.getCode(), "Not found"))
                .when(updateBiologicalObjectMeshId).execute("missing-id", "D016159");

        ApiException ex = assertThrows(ApiException.class, () -> controller.updateMeshId(request));
        assertEquals(GeneralError.NOT_FOUND, ex.getGeneralError());
    }

    @Test
    @DisplayName("Should return mapped BiologicalObjectDetailDTO on find")
    void shouldReturnMappedDetailDtoOnFind() {
        BiologicalObject model = BiologicalObject.builder().id("bio-1").symbol("TP53").name("Tumor protein p53").build();
        when(findBiologicalObject.execute("symbol", "TP53")).thenReturn(model);

        BiologicalObjectDetailDTO dto = controller.find("symbol", "TP53");

        assertNotNull(dto);
        assertEquals("bio-1", dto.id());
        assertEquals("TP53", dto.symbol());
    }
}
