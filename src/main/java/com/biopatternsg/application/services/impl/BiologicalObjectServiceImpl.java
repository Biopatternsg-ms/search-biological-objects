package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BuildBiologicalObjectService;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class BiologicalObjectServiceImpl implements BuildBiologicalObjectService {

    private final HGNCRepository hgncRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public List<BiologicalObject> execute(String geneSymbol) {
        var biologicalObject = build(geneSymbol);
        return biologicalObjectRepository.save(biologicalObject);
    }

    /*
    @Override
    public BiologicalObject execute(TranscriptionFactor transcriptionFactor) {
        var biologicalObject = build(transcriptionFactor.name());
        biologicalObject.setTranscriptionFactor(transcriptionFactor);
        return biologicalObjectRepository.save(biologicalObject);
    }
    */

    private List<BiologicalObject> build(String geneSymbol) {
        List<BiologicalObject> biologicalObject = new ArrayList<>();
        var hgncResponse = hgncRepository.findHGNCInformation(geneSymbol);

        if (hgncResponse != null) {
            hgncResponse.forEach(object -> {

                BiologicalObject oneObject = BiologicalObject.builder()
                        .id(object.getId())
                        .symbol(object.getSymbol())
                        .name(object.getName())
                        .locusType(object.getLocusType())
                        .ensemblGeneId(object.getEnsemblGeneId())
                        .synonym(object.getSynonym())
                        .geneFamily(object.getGeneFamily())
                        .build();

                biologicalObject.add(oneObject);
            });
        }
        return biologicalObject;
    }

}
