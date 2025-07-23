package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.infrastructure.external_services.QueryHGNC;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.Docs;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class HGNCRepositoryAdapter implements HGNCRepository {

    private final QueryHGNC queryHGNC;

    @Override
    public List<HGNCResponse> findHGNCInformation(String geneSymbol) {
        var symbols = getSymbols(geneSymbol);
        return symbols.stream()
                .map(this::getHgncResponse)
                .toList();
    }

    private HGNCResponse getHgncResponse(String symbol) {
        var hgncInformation = queryHGNC.fetch(symbol);
        var values = hgncInformation.response().docs().getFirst();

        return HGNCResponse.builder()
                .id(values.hgncId())
                .name(values.name())
                .symbol(values.symbol())
                .ensemblGeneId(values.ensemblGeneId())
                .locusType(values.locusType())
                .synonym(getSynonyms(values))
                .build();
    }

    private static List<String> getSynonyms(com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.Docs values) {
        List<String> combinedSynonyms = new ArrayList<>();
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






