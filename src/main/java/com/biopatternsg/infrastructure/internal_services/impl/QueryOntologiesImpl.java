package com.biopatternsg.infrastructure.internal_services.impl;

import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.infrastructure.clients.internal_clients.OntologiesHttpClient;
import com.biopatternsg.infrastructure.internal_services.QueryOntologies;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class QueryOntologiesImpl implements QueryOntologies {

    private final OntologiesHttpClient ontologiesHttpClient;

    public QueryOntologiesImpl(@RestClient OntologiesHttpClient ontologiesHttpClient) {
        this.ontologiesHttpClient = ontologiesHttpClient;
    }

    @Override
    public Response buildGeneOntologyTree(GeneOntology geneOntology) {
        try {
            return ontologiesHttpClient.buildGeneOntologyTree(geneOntology);
        } catch (Exception e) {
            log.error("Error building gene ontology tree", e.getMessage());
            return Response.serverError().build();
        }
    }
}
