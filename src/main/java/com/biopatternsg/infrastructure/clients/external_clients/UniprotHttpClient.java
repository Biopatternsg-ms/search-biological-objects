package com.biopatternsg.infrastructure.clients.external_clients;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.ListResponse;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;

@RegisterRestClient(configKey = "uniprot-api")
public interface UniprotHttpClient {

    String DEFAULT_SORT = "accession desc";

    @GET
    @Path("uniprotkb/stream")
    @Produces(MediaType.APPLICATION_JSON)
    ListResponse search(@QueryParam("query") String label, @QueryParam("sort") String sort);

    @GET
    @Path("uniprotkb/{label}.json")
    @Produces(MediaType.APPLICATION_JSON)
    Response get(@PathParam("label") String uniprotId);
}
