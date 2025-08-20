package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BuildBiologicalObjectService;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.models.external_entities.UniprotResponse;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.domain.port.out.external_repositories.UniprotRepository;
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
    private final UniprotRepository uniprotRepository;
    private static final int FIRST_VALUE = 0;

    @Override
    public List<BiologicalObject> execute(String geneSymbol) {
        var biologicalObject = build(geneSymbol);
        return biologicalObjectRepository.save(biologicalObject);
    }

    private List<BiologicalObject> build(String geneSymbol) {
        var biologicalObjects = addHGNCInformation(geneSymbol);
        biologicalObjects.forEach(biologicalObject -> addUniprotInformation(biologicalObject, biologicalObject.getUniprotId()));
        return biologicalObjects;
    }

    private List<BiologicalObject> addHGNCInformation(String geneSymbol) {
        var hgncResponse = hgncRepository.findHGNCInformation(geneSymbol);

        return hgncResponse.stream().map(hgnc ->
                BiologicalObject.builder()
                        .id(hgnc.getId())
                        .symbol(hgnc.getSymbol())
                        .name(hgnc.getName())
                        .locusType(hgnc.getLocusType())
                        .ensemblGeneId(hgnc.getEnsemblGeneId())
                        .synonyms(hgnc.getSynonyms())
                        .uniprotId(hgnc.getUniprotId())
                        .geneFamilies(hgnc.getGeneFamilies())
                        .build()
        ).toList();
    }

    private void addUniprotInformation(BiologicalObject biologicalObject, String uniprotId) {
        var uniprotResponse = uniprotRepository.findInfo(uniprotId);
        biologicalObject.getSynonyms().addAll(uniprotResponse.getSynonyms());
        addGOCodes(biologicalObject, uniprotResponse);
    }

    private void addGOCodes(BiologicalObject biologicalObject, UniprotResponse uniprotResponse) {
        GeneOntology ontologyLists = new GeneOntology();
        ontologyLists.setBiologicalProcess(uniprotResponse.getGoBp());
        ontologyLists.setMolecularFunction(uniprotResponse.getGoMf());
        ontologyLists.setCellularComponent(uniprotResponse.getGoCc());
        biologicalObject.setGeneOntology(ontologyLists);
    }

}
