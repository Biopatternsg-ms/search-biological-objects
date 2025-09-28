package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.impl.BiologicalObjectStrategy.BiologicalObjectContext;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectUseCase implements FindBiologicalObject {

    private final BiologicalObjectContext biologicalObjectContext;

    @Override
    public BiologicalObject execute(String type, String value) {

        var context = biologicalObjectContext.load(type);
        return context.execute(value);
    }

    /*
    @Override
    public BiologicalObject execute(TranscriptionFactor transcriptionFactor) {
        return buildBiologicalObjectService.execute(transcriptionFactor);
    }
    */
}
