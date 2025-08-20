package com.biopatternsg.infrastructure.clients.external_clients;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;

@RegisterRestClient(configKey = "uniprot-api")
public interface UniprotHttpClient {

    String DEFAULT_FIELDS = "accession,protein_name,go_p,go_c,go_f";
    String DEFAULT_SORT = "accession desc";

    @GET
    @Path("uniprotkb/stream")
    @Produces(MediaType.APPLICATION_JSON)
    Response search(@QueryParam("query") String label,@QueryParam("fields") String fields,@QueryParam("sort") String sort);
}
