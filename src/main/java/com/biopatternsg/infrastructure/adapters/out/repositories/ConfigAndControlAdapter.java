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
package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.enums.PipelineSteps;
import com.biopatternsg.domain.enums.Status;
import com.biopatternsg.domain.port.out.repositories.ConfigAndControlRepository;
import com.biopatternsg.infrastructure.dtos.PipelineStepInternalRequest;
import com.biopatternsg.infrastructure.internal_services.QueryConfigAndControl;
import com.biopatternsg.infrastructure.session.SessionUtil;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ConfigAndControlAdapter implements ConfigAndControlRepository {

    private final SessionUtil sessionUtil;
    private final QueryConfigAndControl queryConfigAndControl;

    @Override
    public void updatePipelineStep(String pipelineId, PipelineSteps pipelineStep, Status status) {

        var pipelineStepRequest = updatePipelineStepBuild(pipelineId, pipelineStep, status);
        queryConfigAndControl.updatePipelineStep(pipelineStepRequest, sessionUtil.getUserId());
    }

    private PipelineStepInternalRequest updatePipelineStepBuild(String pipelineId, PipelineSteps pipelineStep, Status status){

        return PipelineStepInternalRequest.builder()
                .id(pipelineId)
                .step(pipelineStep)
                .status(status)
                .build();
    }
}
