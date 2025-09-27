package com.biopatternsg.infrastructure.clients.external_clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.FetchResponse;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.SearchResponse;

@RegisterRestClient(configKey = "hgnc-api")
public interface HgncHttpClient {

    @GET
    @Path("search/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    SearchResponse search(@PathParam("label") String label);

    @GET
    @Path("fetch/symbol/{symbol}")
    @Produces(MediaType.APPLICATION_JSON)
    FetchResponse fetchSymbol(@PathParam("symbol") String symbol);

    @GET
    @Path("fetch/hgnc_id/{hgncId}")
    @Produces(MediaType.APPLICATION_JSON)
    FetchResponse fetchId(@PathParam("hgncId") String symbol);

    @GET
    @Path("fetch/uniprot_ids/{uniprotId}")
    @Produces(MediaType.APPLICATION_JSON)
    FetchResponse fetchUniprotId(@PathParam("uniprotId") String uniprotId);
}
