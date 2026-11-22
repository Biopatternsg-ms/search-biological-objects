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
package com.biopatternsg.infrastructure.adapters.mappers;

import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectConfigDTO;
import com.biopatternsg.infrastructure.adapters.dtos.LaunchPipelineRequest;

import java.util.List;

/**
 * Stateless pure mapper converting pipeline delivery DTOs into domain models.
 */
public final class PipelineDtoMapper {

    private PipelineDtoMapper() {
    }

    public static PipelineConfig toDomain(LaunchPipelineRequest request) {
        if (request == null) {
            return null;
        }
        return PipelineConfig.builder()
                .pipelineId(request.pipelineId())
                .levels(request.levels())
                .maxComplexes(request.maxComplexes())
                .expertObjects(toExpertObjects(request.expertObjects()))
                .transcriptionFactorConfig(TranscriptionFactorMapper.toDomain(request.transcriptionFactorConfig()))
                .build();
    }

    private static List<BiologicalObjectConfig> toExpertObjects(List<BiologicalObjectConfigDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream()
                .map(PipelineDtoMapper::toExpertObject)
                .toList();
    }

    private static BiologicalObjectConfig toExpertObject(BiologicalObjectConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        return BiologicalObjectConfig.builder()
                .uniprotId(dto.uniprotId())
                .hgncId(dto.hgncId())
                .symbol(dto.symbol())
                .build();
    }
}
