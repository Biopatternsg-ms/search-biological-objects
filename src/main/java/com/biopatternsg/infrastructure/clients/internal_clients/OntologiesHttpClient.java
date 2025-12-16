package com.biopatternsg.infrastructure.clients.internal_clients;

import com.biopatternsg.domain.models.GeneOntology;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "ontologies-api")
public interface OntologiesHttpClient {

    @POST
    @Path("/gene-ontology/build-tree")
    Response buildGeneOntologyTree(@RequestBody GeneOntology geneOntology);
}
