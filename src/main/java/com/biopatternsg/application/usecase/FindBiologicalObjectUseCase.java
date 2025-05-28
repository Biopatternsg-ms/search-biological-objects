package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.BuildBiologicalObjectService;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectUseCase implements FindBiologicalObject {

    private final BuildBiologicalObjectService buildBiologicalObjectService;

    @Override
    public BiologicalObject execute(String label) {
        return buildBiologicalObjectService.execute(label);
    }

}
