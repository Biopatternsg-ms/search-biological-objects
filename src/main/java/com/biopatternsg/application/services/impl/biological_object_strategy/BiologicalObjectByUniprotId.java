package com.biopatternsg.application.services.impl.biological_object_strategy;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.models.external_entities.UniprotResponse;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.domain.port.out.external_repositories.UniprotRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectByUniprotId implements BuildBiologicalObjectStrategy, ChainResponsibility {

    private final HGNCRepository hgncRepository;
    private final UniprotRepository uniprotRepository;
    private ChainResponsibility next;

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
        biologicalObject.setHgncId(hgncResponse.getId());
        biologicalObject.setLocusType(hgncResponse.getLocusType());
        biologicalObject.setEnsemblGeneId(hgncResponse.getEnsemblGeneId());
        biologicalObject.getSynonyms().addAll(hgncResponse.getSynonyms());
    }

    @Override
    public void setNext(ChainResponsibility chainResponsibility) {
        this.next = chainResponsibility;
    }

    @Override
    public ChainResponsibility getNext() {
        return this.next;
    }

    @Override
    public BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig) {

        if(biologicalObjectConfig.getUniprotId() == null || biologicalObjectConfig.getUniprotId().isEmpty()){
            return next.request(biologicalObjectConfig);
        }

        var biologicalObject = execute(biologicalObjectConfig.getUniprotId());
        if(biologicalObject == null){
            return next.request(biologicalObjectConfig);
        }

        return biologicalObject;
    }
}
