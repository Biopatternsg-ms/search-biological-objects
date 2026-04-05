package com.biopatternsg.infrastructure.clients.external_clients;

import com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Response;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "pdbe-api")
public interface PdbHttpClient {

    @GET
    @Path("/api/v2/complex/details/{uniprotId}")
    @Produces(MediaType.APPLICATION_JSON)
    Response search(@PathParam("uniprotId") String uniprotId,
                    @QueryParam("id_type") String idType);
}
