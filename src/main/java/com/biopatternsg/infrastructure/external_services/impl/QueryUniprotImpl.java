package com.biopatternsg.infrastructure.external_services.impl;

import com.biopatternsg.infrastructure.clients.external_clients.UniprotHttpClient;
import com.biopatternsg.infrastructure.external_services.QueryUniprot;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.ListResponse;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class QueryUniprotImpl implements QueryUniprot{

    private final UniprotHttpClient uniprotHttpClient;

    public QueryUniprotImpl(@RestClient UniprotHttpClient uniprotHttpClient) {
        this.uniprotHttpClient = uniprotHttpClient;
    }

    @Override
    public ListResponse search(String label) {
        return uniprotHttpClient.search(label, UniprotHttpClient.DEFAULT_SORT);
    }

    @Override
    public Response get(String uniprotId) {
        return uniprotHttpClient.get(uniprotId);
    }
}
