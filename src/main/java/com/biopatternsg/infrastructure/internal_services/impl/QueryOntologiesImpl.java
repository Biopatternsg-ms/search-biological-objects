package com.biopatternsg.infrastructure.internal_services.impl;

import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.infrastructure.clients.internal_clients.OntologiesHttpClient;
import com.biopatternsg.infrastructure.internal_services.QueryOntologies;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class QueryOntologiesImpl implements QueryOntologies {

    private final OntologiesHttpClient ontologiesHttpClient;

    public QueryOntologiesImpl(@RestClient OntologiesHttpClient ontologiesHttpClient) {
        this.ontologiesHttpClient = ontologiesHttpClient;
    }

    @Override
    public Response buildGeneOntologyTree(GeneOntology geneOntology) {
        return ontologiesHttpClient.buildGeneOntologyTree(geneOntology);
    }
}
