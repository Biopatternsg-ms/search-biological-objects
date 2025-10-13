package com.biopatternsg.infrastructure.clients.external_clients;

import com.biopatternsg.infrastructure.external_services.dtos.pdbe.Response;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "pdbe-api")
public interface PdbHttpClient {

    @GET
    @Path("uniprot/complex/{uniprotId}")
    @Produces(MediaType.APPLICATION_JSON)
    Response search(@PathParam("uniprotId") String uniprotId);
}
