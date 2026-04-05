package com.biopatternsg.infrastructure.external_services.impl;

import com.biopatternsg.infrastructure.clients.external_clients.TFBindHttpClient;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.infrastructure.external_services.QueryTFBIND;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class QueryTFBINDImpl implements QueryTFBIND {

    private final TFBindHttpClient tfBindHttpClient;

    public QueryTFBINDImpl(@RestClient TFBindHttpClient tfBindHttpClient) {
        this.tfBindHttpClient = tfBindHttpClient;
    }

    @Override
    @Retry
    public String getByPromoterRegion(PromoterRegionRequest promoterRegion) {

        try{
            return tfBindHttpClient.getByPromoterRegion(">COMMENTS\n" + promoterRegion.promoterRegion());
        } catch (Exception e) {
            log.info("Error con tfbind: {}", e.getMessage());
            return "";
        }
    }
}
