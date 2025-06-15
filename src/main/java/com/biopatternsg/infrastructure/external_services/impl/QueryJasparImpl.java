package com.biopatternsg.infrastructure.external_services.impl;

import com.biopatternsg.infrastructure.clients.external_clients.JasparHttpClient;
import com.biopatternsg.infrastructure.dtos.JasparRegionData;
import com.biopatternsg.infrastructure.dtos.JasparRegion;
import com.biopatternsg.infrastructure.external_services.QueryJaspar;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class QueryJasparImpl implements QueryJaspar {

    private final JasparHttpClient jasparHttpClient;

    public QueryJasparImpl(@RestClient JasparHttpClient jasparHttpClient) {
        this.jasparHttpClient = jasparHttpClient;
    }

    @Override
    public JasparRegionData getDataFromJasparSource(JasparRegion jasparRequest) {
        return jasparHttpClient.getRegionData(jasparRequest);
    }
}
