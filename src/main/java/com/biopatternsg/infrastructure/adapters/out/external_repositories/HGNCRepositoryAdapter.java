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

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class HGNCRepositoryAdapter implements HGNCRepository {

    private final QueryHGNC queryHGNC;
    private final List<HGNCResponse> hgncResponses = new ArrayList<>();

    @Override
    public List<HGNCResponse> findHGNCInformation(String geneSymbol) {
        var hgncSymbol = queryHGNC.search(geneSymbol);

        var symbolList = hgncSymbol.response().docs().stream()
                .filter(doc -> doc.score() >= hgncSymbol.response().maxScore())
                .map(Docs::symbol)
                .toList();

        symbolList.forEach(symbol -> {

            var hgncInformation = queryHGNC.fetch(symbol);
            var hgnc = new HGNCResponse();

            hgnc.setId(hgncInformation.response().docs().get(0).hgncId());
            hgnc.setName(hgncInformation.response().docs().get(0).name());
            hgnc.setSymbol(hgncInformation.response().docs().get(0).symbol());
            hgnc.setEnsemblGeneId(hgncInformation.response().docs().get(0).ensemblGeneId());
            hgnc.setLocusType(hgncInformation.response().docs().get(0).locusType());
            hgnc.getSynonym().addAll(hgncInformation.response().docs().get(0).aliasName());
            hgnc.getSynonym().add(hgncInformation.response().docs().get(0).cosmic());

            hgncResponses.add(hgnc);
        });

        return hgncResponses;
    }
}






