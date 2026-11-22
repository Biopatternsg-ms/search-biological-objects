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

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDTO;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDetailDTO;

import java.util.List;

/**
 * Stateless pure mapper converting BiologicalObject domain models to REST delivery DTOs.
 */
public final class BiologicalObjectDtoMapper {

    private BiologicalObjectDtoMapper() {
    }

    public static BiologicalObjectDetailDTO toDetailDto(BiologicalObject model) {
        if (model == null) {
            return null;
        }
        return new BiologicalObjectDetailDTO(
                model.getId(),
                model.getSymbol(),
                model.getName(),
                model.getLocusType(),
                model.getEnsemblGeneId(),
                model.getHgncId(),
                model.getUniprotId(),
                model.getMeshId(),
                model.getSynonyms(),
                model.getGeneOntology(),
                model.getGeneFamilies(),
                model.getTissues(),
                model.getPrevSymbol(),
                TranscriptionFactorMapper.toDto(model.getTranscriptionFactor())
        );
    }

    public static BiologicalObjectDTO toSummaryDto(BiologicalObject model) {
        if (model == null) {
            return null;
        }
        return BiologicalObjectDTO.fromDomain(model);
    }

    public static List<BiologicalObjectDTO> toSummaryDtoList(List<BiologicalObject> models) {
        if (models == null) {
            return List.of();
        }
        return models.stream()
                .map(BiologicalObjectDtoMapper::toSummaryDto)
                .toList();
    }
}
