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
import com.biopatternsg.domain.port.in.FindBiologicalObjectFatherBrothersAndSons;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectFatherBrothersAndSonsUseCase implements FindBiologicalObjectFatherBrothersAndSons {

    private final MinedObjectRepository minedObjectRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public List<BiologicalObject> execute(String pipelineId, String biologicalObjectId) {
        MinedObject currentMinedObject = minedObjectRepository.find(biologicalObjectId, pipelineId);
        if (currentMinedObject == null) {
            return List.of();
        }

        BiologicalObject currentBiologicalObject = biologicalObjectRepository.findById(currentMinedObject.getBiologicalObjectId());
        if (currentBiologicalObject == null) {
            return List.of();
        }

        Map<String, BiologicalObject> biologicalObjectsMap = new HashMap<>();
        biologicalObjectsMap.put(currentBiologicalObject.getId(), currentBiologicalObject);

        addFatherAndBrothers(biologicalObjectsMap, currentMinedObject, pipelineId);
        addSons(biologicalObjectsMap, currentBiologicalObject.getId(), pipelineId);

        return List.copyOf(biologicalObjectsMap.values());
    }

    private void addFatherAndBrothers(Map<String, BiologicalObject> result, MinedObject currentMinedObject, String pipelineId) {
        String parentId = currentMinedObject.getBiologicalObjectParentId();
        if (parentId == null) {
            return;
        }

        BiologicalObject currentFather = biologicalObjectRepository.findById(parentId);
        if (currentFather != null) {
            result.put(currentFather.getId(), currentFather);
        }

        findBrothers(currentMinedObject, pipelineId)
                .forEach(brother -> result.put(brother.getId(), brother));
    }

    private List<BiologicalObject> findBrothers(MinedObject currentMinedObject, String pipelineId) {
        List<MinedObject> minedBrothers = minedObjectRepository.findByParentId(
                currentMinedObject.getBiologicalObjectParentId(),
                pipelineId
        );
        List<String> brotherIds = minedBrothers.stream()
                .map(MinedObject::getBiologicalObjectId)
                .filter(id -> !Objects.equals(id, currentMinedObject.getBiologicalObjectId()))
                .toList();

        if (brotherIds.isEmpty()) {
            return List.of();
        }
        return biologicalObjectRepository.findByIds(brotherIds);
    }

    private void addSons(Map<String, BiologicalObject> result, String biologicalObjectId, String pipelineId) {
        List<MinedObject> minedSons = minedObjectRepository.findByParentId(biologicalObjectId, pipelineId);
        List<String> sonIds = minedSons.stream()
                .map(MinedObject::getBiologicalObjectId)
                .toList();

        if (sonIds.isEmpty()) {
            return;
        }
        biologicalObjectRepository.findByIds(sonIds)
                .forEach(son -> result.put(son.getId(), son));
    }
}
