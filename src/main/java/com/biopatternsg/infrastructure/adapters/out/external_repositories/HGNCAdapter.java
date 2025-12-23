package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.infrastructure.external_services.QueryHGNC;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.Docs;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class HGNCAdapter implements HGNCRepository {

    private final QueryHGNC queryHGNC;

    @Override
    public HGNCResponse findSymbolInformation(String geneSymbol) {
        var symbol = searchSymbol(geneSymbol);
        return getGeneSymbolInformation(symbol);
    }

    @Override
    public HGNCResponse findHgncIdInformation(String hgncId) {
        return getHGNCIdInformation(hgncId);
    }

    @Override
    public HGNCResponse findUniprotIdInformation(String uniprotId) {
        return getUniprotIdInformation(uniprotId);
    }

    private HGNCResponse getGeneSymbolInformation(String symbol) {

        if(symbol == null){
            return null;
        }
        var hgncInformation = queryHGNC.fetchSymbol(symbol);
        var values = hgncInformation.response().docs().getFirst();
        return formatHGNCInformation(values);
    }

    private HGNCResponse getHGNCIdInformation(String hgncId){

        var hgncInformation = queryHGNC.fetchId(hgncId);
        if(hgncInformation.response().docs().isEmpty()){
            return null;
        }
        var values = hgncInformation.response().docs().getFirst();
        return formatHGNCInformation(values);
    }

    private HGNCResponse getUniprotIdInformation(String uniprotId){

        var hgncInformation = queryHGNC.fetchUniprotId(uniprotId);
        if(hgncInformation.response().docs().isEmpty()){
            return null;
        }
        var values = hgncInformation.response().docs().getFirst();
        return formatHGNCInformation(values);
    }

    private String searchSymbol(String geneSymbol) {

        var symbols = queryHGNC.search(geneSymbol);
        return symbols.response().docs().stream()
                .filter(doc -> geneSymbol.equals(doc.symbol()))
                .max(Comparator.comparingDouble(com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.Docs::score))
                .map(com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.Docs::symbol)
                .orElse(null);
    }

    private HGNCResponse formatHGNCInformation(Docs values){

        return HGNCResponse.builder()
                .id(values.hgncId())
                .name(values.name())
                .symbol(values.symbol())
                .ensemblGeneId(values.ensemblGeneId())
                .locusType(values.locusType())
                .synonyms(getSynonyms(values))
                .uniprotId(values.uniprotIds().getFirst())
                .build();
    }

    private Set<String> getSynonyms(Docs values) {
        Set<String> combinedSynonyms = new HashSet<>();
        Optional.ofNullable(values.aliasName()).ifPresent(combinedSynonyms::addAll);
        Optional.ofNullable(values.cosmic()).ifPresent(combinedSynonyms::add);
        return combinedSynonyms;
    }
}






