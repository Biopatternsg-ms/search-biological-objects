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
public class BuildBiologicalObjectByHgncId implements BuildBiologicalObjectStrategy {

    private final HGNCRepository hgncRepository;
    private final UniprotRepository uniprotRepository;

    @Override
    public BiologicalObject execute(String value) {

        var hgncResponse = hgncRepository.findHgncIdInformation(value);
        var biologicalObject = formatHGNCInformation(hgncResponse);
        addUniprotInformation(biologicalObject, biologicalObject.getUniprotId());

        return biologicalObject;
    }

    private BiologicalObject formatHGNCInformation(HGNCResponse hgncResponse) {

        return BiologicalObject.builder()
                        .id(hgncResponse.getId())
                        .symbol(hgncResponse.getSymbol())
                        .name(hgncResponse.getName())
                        .locusType(hgncResponse.getLocusType())
                        .ensemblGeneId(hgncResponse.getEnsemblGeneId())
                        .synonyms(hgncResponse.getSynonyms())
                        .uniprotId(hgncResponse.getUniprotId())
                        .geneFamilies(hgncResponse.getGeneFamilies())
                        .build();
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
