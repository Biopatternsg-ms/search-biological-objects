package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.BuildBiologicalObjectService;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectUseCase implements FindBiologicalObject {

    private final BuildBiologicalObjectService buildBiologicalObjectService;

    @Override
    public List<BiologicalObject> execute(String label) {
        return buildBiologicalObjectService.execute(label);
    }

    /*
    @Override
    public BiologicalObject execute(TranscriptionFactor transcriptionFactor) {
        return buildBiologicalObjectService.execute(transcriptionFactor);
    }
    */

}
