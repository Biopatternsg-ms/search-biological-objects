package com.biopatternsg.infrastructure.clients.external_clients;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "blat-api")
public interface BlatHttpClient {

    @POST
    @Path("/cgi-bin/hgBlat")
    String getByPromoterRegion(
            @QueryParam("type") String type,
            @QueryParam("userSeq") String promoterRegion);

}
