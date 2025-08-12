package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BuildBiologicalObjectService;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.domain.port.out.external_repositories.UniprotRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@ApplicationScoped
public class BiologicalObjectServiceImpl implements BuildBiologicalObjectService {

    private final HGNCRepository hgncRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final UniprotRepository uniprotObject;
    private static final int FIRST_VALUE = 0;

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
            hgncResponse.forEach(object -> biologicalObject.add(addUniprotToBiologicalObject(object)));
        }

        return biologicalObject;
    }

    private BiologicalObject addUniprotToBiologicalObject(HGNCResponse object) {

        var uniprotResponse = uniprotObject.findInfo(object.getUniprotIds().get(FIRST_VALUE));
        object.getSynonyms().addAll(uniprotResponse.getSynonyms());

        return BiologicalObject.builder()
                .id(object.getId())
                .symbol(object.getSymbol())
                .name(object.getName())
                .locusType(object.getLocusType())
                .ensemblGeneId(object.getEnsemblGeneId())
                .synonyms(object.getSynonyms())
                .geneFamilies(object.getGeneFamilies())
                .build();
    }
}
