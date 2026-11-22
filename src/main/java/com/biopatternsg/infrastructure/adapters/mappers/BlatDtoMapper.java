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

import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.infrastructure.adapters.dtos.BlatSearchOptionDTO;

import java.util.List;

/**
 * Stateless pure mapper converting BlatSearchOptionsResponse domain records into BlatSearchOptionDTOs.
 */
public final class BlatDtoMapper {

    private BlatDtoMapper() {
    }

    public static BlatSearchOptionDTO toDto(BlatSearchOptionsResponse model) {
        if (model == null) {
            return null;
        }
        return new BlatSearchOptionDTO(
                model.identity(),
                model.chromosome(),
                model.strand(),
                model.start(),
                model.end()
        );
    }

    public static List<BlatSearchOptionDTO> toDtoList(List<BlatSearchOptionsResponse> models) {
        if (models == null) {
            return List.of();
        }
        return models.stream()
                .map(BlatDtoMapper::toDto)
                .toList();
    }
}
