package com.biopatternsg.infrastructure.external_services.impl;

import com.biopatternsg.infrastructure.clients.external_clients.BlatHttpClient;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.infrastructure.external_services.QueryBlat;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class QueryBlatImpl implements QueryBlat {

    private final BlatHttpClient blatHttpClient;

    public QueryBlatImpl(@RestClient BlatHttpClient blatHttpClient) {
        this.blatHttpClient = blatHttpClient;
    }

    @Override
    public String getByPromoterJasparRegion(PromoterRegionRequest promoterRegionDTO) {
        return blatHttpClient.getByPromoterRegion("BLAT%27s+guess", promoterRegionDTO.promoterRegion());
    }
}
