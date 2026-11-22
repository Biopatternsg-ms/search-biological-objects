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

import com.biopatternsg.application.services.PipelineService;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class LaunchPipelineUseCaseTest {

    @Mock
    private PipelineService pipelineService;

    @InjectMocks
    private LaunchPipelineUseCase useCase;

    @Test
    @DisplayName("Should delegate pipeline execution to PipelineService")
    void shouldDelegateToPipelineService() {
        PipelineConfig config = PipelineConfig.builder()
                .pipelineId("pipe-1")
                .levels(2)
                .expertObjects(List.of())
                .build();

        useCase.execute(config);

        verify(pipelineService).execute(config);
        verifyNoMoreInteractions(pipelineService);
    }
}
