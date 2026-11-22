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

import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.infrastructure.adapters.dtos.ComplexDTO;

import java.util.List;

/**
 * Stateless pure mapper converting Complex domain models into ComplexDTOs.
 */
public final class ComplexDtoMapper {

    private ComplexDtoMapper() {
    }

    public static ComplexDTO toDto(Complex model) {
        if (model == null) {
            return null;
        }
        return new ComplexDTO(
                model.getComplexPdbId(),
                model.getParticipants(),
                model.getScore()
        );
    }

    public static List<ComplexDTO> toDtoList(List<Complex> models) {
        if (models == null) {
            return List.of();
        }
        return models.stream()
                .map(ComplexDtoMapper::toDto)
                .toList();
    }
}
