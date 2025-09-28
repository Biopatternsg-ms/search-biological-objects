package com.biopatternsg.application.services.impl.BiologicalObjectStrategy;

import com.biopatternsg.application.services.BuildBiologicalObjectStrategy;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.models.external_entities.UniprotResponse;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.domain.port.out.external_repositories.UniprotRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BuildBiologicalObjectByUniprotId implements BuildBiologicalObjectStrategy {

    private final HGNCRepository hgncRepository;
    private final UniprotRepository uniprotRepository;

    @Override
    public BiologicalObject execute(String value) {

        var uniprotResponse = uniprotRepository.findInfo(value);
        var biologicalObject = formatUniprotInformation(uniprotResponse);
        addHgncInformation(biologicalObject, value);

        return biologicalObject;
    }

    private BiologicalObject formatUniprotInformation(UniprotResponse uniprotResponse) {

        GeneOntology ontologyLists = new GeneOntology();
        ontologyLists.setBiologicalProcess(uniprotResponse.getGoBp());
        ontologyLists.setMolecularFunction(uniprotResponse.getGoMf());
        ontologyLists.setCellularComponent(uniprotResponse.getGoCc());

        return BiologicalObject.builder()
                .symbol(uniprotResponse.getSymbol())
                .name(uniprotResponse.getName())
                .uniprotId(uniprotResponse.getId())
                .synonyms(uniprotResponse.getSynonyms())
                .geneOntology(ontologyLists)
                .build();
    }

    private void addHgncInformation(BiologicalObject biologicalObject, String uniprotId) {

        HGNCResponse hgncResponse = hgncRepository.findUniprotIdInformation(uniprotId);
        biologicalObject.setId(hgncResponse.getId());
        biologicalObject.setLocusType(hgncResponse.getLocusType());
        biologicalObject.setEnsemblGeneId(hgncResponse.getEnsemblGeneId());
        biologicalObject.getSynonyms().addAll(hgncResponse.getSynonyms());
    }
}
