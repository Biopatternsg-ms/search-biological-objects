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

import com.biopatternsg.domain.models.JasparQuery;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.models.pipeline_config.TranscriptionFactorConfig;
import com.biopatternsg.infrastructure.adapters.dtos.TranscriptionFactorConfigRequest;
import com.biopatternsg.infrastructure.adapters.dtos.TranscriptionFactorDTO;
import com.biopatternsg.infrastructure.dtos.JasparRequest;

import java.util.List;

/**
 * Stateless pure mapper for transcription factor requests, domain models, and delivery DTOs.
 */
public final class TranscriptionFactorMapper {

    private TranscriptionFactorMapper() {
    }

    public static JasparQuery requestToModel(JasparRequest request) {
        if (request == null) {
            return null;
        }
        return new JasparQuery(
                request.genome(),
                request.track(),
                request.chromosome(),
                request.start(),
                request.end(),
                request.strand(),
                request.reliability()
        );
    }

    public static TranscriptionFactorConfig toDomain(TranscriptionFactorConfigRequest request) {
        if (request == null) {
            return null;
        }
        return TranscriptionFactorConfig.builder()
                .promoterRegion(request.promoterRegion())
                .reliability(request.reliability())
                .start(request.start())
                .end(request.end())
                .genome(request.genome())
                .track(request.track())
                .identity(request.identity())
                .chromosome(request.chromosome())
                .strand(request.strand())
                .sources(request.sources())
                .build();
    }

    public static TranscriptionFactorDTO toDto(TranscriptionFactor model) {
        if (model == null) {
            return null;
        }
        return new TranscriptionFactorDTO(
                model.name(),
                model.reliability(),
                model.number(),
                model.sign(),
                model.chain(),
                model.matrix(),
                model.source()
        );
    }

    public static List<TranscriptionFactorDTO> toDtoList(List<TranscriptionFactor> models) {
        if (models == null) {
            return List.of();
        }
        return models.stream()
                .map(TranscriptionFactorMapper::toDto)
                .toList();
    }
}
