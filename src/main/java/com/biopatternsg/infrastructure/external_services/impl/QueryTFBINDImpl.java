package com.biopatternsg.infrastructure.external_services.impl;

import com.biopatternsg.infrastructure.clients.external_clients.TFBindHttpClient;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.infrastructure.external_services.QueryTFBIND;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class QueryTFBINDImpl implements QueryTFBIND {

    private final TFBindHttpClient tfBindHttpClient;

    public QueryTFBINDImpl(@RestClient TFBindHttpClient tfBindHttpClient) {
        this.tfBindHttpClient = tfBindHttpClient;
    }

    @Override
    public String getByPromoterRegion(PromoterRegionRequest promoterRegion) {
        return tfBindHttpClient.getByPromoterRegion("> COMMENTS\r\n" + promoterRegion.promoterRegion());
    }
}
