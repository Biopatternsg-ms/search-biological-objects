package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.infrastructure.external_services.QueryHGNC;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class HGNCRepositoryAdapter implements HGNCRepository {

    private final QueryHGNC queryHGNC;

    @Override
    public HGNCResponse findHGNCInformation(String geneSymbol) {
        var hgncSymbol = queryHGNC.search(geneSymbol);
        var hgncGeneInformation = queryHGNC.fetch(hgncSymbol.getSymbol());

        return HGNCResponse.builder()
                .name(hgncGeneInformation.getName())
                .build();
    }
}
