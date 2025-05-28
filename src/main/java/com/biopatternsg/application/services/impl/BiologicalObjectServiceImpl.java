package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BuildBiologicalObjectService;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class BiologicalObjectServiceImpl implements BuildBiologicalObjectService {

    private final HGNCRepository hgncRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public BiologicalObject execute(String geneSymbol) {
        var biologicalObject = build(geneSymbol);
        return biologicalObjectRepository.save(biologicalObject);
    }

    @Override
    public BiologicalObject execute(TranscriptionFactor transcriptionFactor) {
        var biologicalObject = build(transcriptionFactor.name());
        biologicalObject.setTranscriptionFactor(transcriptionFactor);
        return biologicalObjectRepository.save(biologicalObject);
    }

    private BiologicalObject build(String geneSymbol) {
        var biologicalObject = new BiologicalObject();
        var hgncResponse = hgncRepository.findHGNCInformation(geneSymbol);

        if (hgncResponse != null) {
            biologicalObject.setName(hgncResponse.getName());
        }
        return biologicalObject;
    }

}
