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

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.port.in.FindBiologicalObjectsByPipelineAndLevel;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDTO;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectsByPipelineIdAndLevelUseCase implements FindBiologicalObjectsByPipelineAndLevel {

    private final MinedObjectRepository minedObjectRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public List<BiologicalObjectDTO> execute(String pipelineId, int level) {

        List<MinedObject> byLevel = minedObjectRepository.findByLevel(level, pipelineId);

        if(byLevel.isEmpty()) {
            return List.of();
        }

        List<String> minedObjectsIds = byLevel.stream().map(MinedObject::getBiologicalObjectId).toList();
        List<BiologicalObject> byIds = biologicalObjectRepository.findByIds(minedObjectsIds)
                .stream().filter(biologicalObject -> isValidName(biologicalObject) || isValidSynonyms(biologicalObject)).toList();

        return byIds.stream().map(BiologicalObjectDTO::fromDomain).toList();
    }

    private boolean isValidName(BiologicalObject biologicalObject) {
        return biologicalObject.getName() != null && !biologicalObject.getName().isEmpty();
    }

    private boolean isValidSynonyms(BiologicalObject biologicalObject) {
        return biologicalObject.getSynonyms() != null && !biologicalObject.getSynonyms().isEmpty();
    }

}
