package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.infrastructure.external_services.QueryHGNC;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.Docs;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class HGNCRepositoryAdapter implements HGNCRepository {

    private final QueryHGNC queryHGNC;
    private static final int FIRST_VALUE = 0;

    @Override
    public List<HGNCResponse> findHGNCInformation(String geneSymbol) {
        var symbols = getSymbols(geneSymbol);
        return symbols.stream()
                .map(this::getHgncResponse)
                .toList();
    }

    private HGNCResponse getHgncResponse(String symbol) {

        var hgncInformation = queryHGNC.fetch(symbol);
        var values = hgncInformation.response().docs().get(FIRST_VALUE);

        return HGNCResponse.builder()
                .id(values.hgncId())
                .name(values.name())
                .symbol(values.symbol())
                .ensemblGeneId(values.ensemblGeneId())
                .locusType(values.locusType())
                .synonyms(getSynonyms(values))
                .uniprotId(values.uniprotIds().get(FIRST_VALUE))
                .build();
    }

    private Set<String> getSynonyms(com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.Docs values) {
        Set<String> combinedSynonyms = new HashSet<>();
        Optional.ofNullable(values.aliasName()).ifPresent(combinedSynonyms::addAll);
        Optional.ofNullable(values.cosmic()).ifPresent(combinedSynonyms::add);
        return combinedSynonyms;
    }

    private List<String> getSymbols(String geneSymbol) {
        var symbols = queryHGNC.search(geneSymbol);
        return symbols.response().docs().stream()
                .filter(doc -> doc.score() >= symbols.response().maxScore())
                .map(Docs::symbol)
                .toList();
    }
}






