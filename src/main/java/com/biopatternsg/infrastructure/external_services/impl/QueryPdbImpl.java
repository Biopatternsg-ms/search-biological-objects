package com.biopatternsg.infrastructure.external_services.impl;

import com.biopatternsg.infrastructure.clients.external_clients.PdbHttpClient;
import com.biopatternsg.infrastructure.external_services.QueryPdb;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe.Response;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class QueryPdbImpl implements QueryPdb {

    private final PdbHttpClient pdbeHttpClient;

    public QueryPdbImpl(@RestClient PdbHttpClient pdbeHttpClient) {
        this.pdbeHttpClient = pdbeHttpClient;
    }

    @Override
    public Response search(String label) {
        return pdbeHttpClient.search(label);
    }
}
