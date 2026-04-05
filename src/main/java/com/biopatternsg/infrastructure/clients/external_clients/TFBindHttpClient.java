package com.biopatternsg.infrastructure.clients.external_clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "tfbind-api")
public interface TFBindHttpClient {

    @GET
    String getByPromoterRegion(@QueryParam("seq") String sequence);
}
