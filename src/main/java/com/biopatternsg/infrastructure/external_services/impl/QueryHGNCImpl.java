package com.biopatternsg.infrastructure.external_services.impl;

import com.biopatternsg.infrastructure.clients.external_clients.HgncHttpClient;
import com.biopatternsg.infrastructure.external_services.QueryHGNC;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.FetchResponse;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.SearchResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class QueryHGNCImpl implements QueryHGNC {

    private final HgncHttpClient hgncHttpClient;

    public QueryHGNCImpl(@RestClient HgncHttpClient hgncHttpClient) {
        this.hgncHttpClient = hgncHttpClient;
    }

    @Override
    public SearchResponse search(String label) {
        return hgncHttpClient.search(label);
    }

    @Override
    public FetchResponse fetch(String symbol) {
        return hgncHttpClient.fetch(symbol);

    }
}
