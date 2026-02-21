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
