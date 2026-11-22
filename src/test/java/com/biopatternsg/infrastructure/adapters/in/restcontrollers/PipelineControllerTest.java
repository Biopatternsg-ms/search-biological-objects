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

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.infrastructure.adapters.dtos.LaunchPipelineRequest;
import com.biopatternsg.infrastructure.adapters.dtos.LaunchPipelineResponse;
import com.biopatternsg.infrastructure.adapters.in.AsyncPipelineDispatcher;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PipelineControllerTest {

    @Mock
    private AsyncPipelineDispatcher asyncPipelineDispatcher;

    @InjectMocks
    private PipelineController controller;

    @Test
    @DisplayName("Should dispatch pipeline asynchronously and return 202 Accepted with LaunchPipelineResponse")
    void shouldDispatchAndReturnAcceptedResponse() {
        LaunchPipelineRequest request = new LaunchPipelineRequest("pipe-42", 2, 5, List.of(), null);

        try (Response response = controller.experiment(request)) {
            assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
            LaunchPipelineResponse body = assertInstanceOf(LaunchPipelineResponse.class, response.getEntity());
            assertEquals("successful launch pipeline pipe-42", body.message());

            ArgumentCaptor<PipelineConfig> captor = ArgumentCaptor.forClass(PipelineConfig.class);
            verify(asyncPipelineDispatcher).dispatch(captor.capture());
            assertEquals("pipe-42", captor.getValue().getPipelineId());
            assertEquals(2, captor.getValue().getLevels());
        }
    }
}
