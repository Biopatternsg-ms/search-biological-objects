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
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectMeshId;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateBiologicalObjectMeshIdUseCase implements UpdateBiologicalObjectMeshId {

    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public void execute(String biologicalObjectId, String meshId) {
        BiologicalObject biologicalObject = biologicalObjectRepository.findById(biologicalObjectId);

        if (biologicalObject == null) {
            log.error("Biological object not found with id: {}", biologicalObjectId);
            return;
        }

        biologicalObject.setMeshId(meshId);
        biologicalObjectRepository.update(biologicalObject);
        log.info("Mesh ID updated successfully for biological object with id: {}", biologicalObjectId);
    }
}
