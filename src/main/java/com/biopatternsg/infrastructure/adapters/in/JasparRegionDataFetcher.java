package com.biopatternsg.infrastructure.adapters.in;

import com.biopatternsg.infrastructure.dtos.JasparRegionDataRequest;
import com.biopatternsg.infrastructure.dtos.JasparRegionData;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Path("/getData/track")
@RegisterRestClient(configKey = "ucsc-genome-api")
public interface JasparRegionDataFetcher {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    JasparRegionData getRegionData(@BeanParam JasparRegionDataRequest jasparRequest);
}
