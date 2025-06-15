package com.biopatternsg.infrastructure.clients.external_clients;

import com.biopatternsg.infrastructure.dtos.JasparRegion;
import com.biopatternsg.infrastructure.dtos.JasparRegionData;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Path("/getData/track")
@RegisterRestClient(configKey = "ucsc-genome-api")
public interface JasparHttpClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    JasparRegionData getRegionData(@BeanParam JasparRegion jasparRequest);
}
