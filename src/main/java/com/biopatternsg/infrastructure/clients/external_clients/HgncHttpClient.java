package com.biopatternsg.infrastructure.clients.external_clients;

import com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.FetchResponse;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.SearchResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "hgnc-api")
public interface HgncHttpClient {

    @GET
    @Path("search/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    SearchResponse search(@PathParam("label") String label);

    @GET
    @Path("fetch/symbol/{symbol}")
    @Produces(MediaType.APPLICATION_JSON)
    FetchResponse fetch(@PathParam("symbol") String symbol);
}
