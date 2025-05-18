package infrastructure.adapters.in;

import infrastructure.dtos.ConsultaJaspar;
import infrastructure.dtos.JasparRegionData;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Path("/getData/track")
@RegisterRestClient(configKey = "ucsc-genome-api")
public interface JasparRegionDataFetcher {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    JasparRegionData getRegionData(@BeanParam ConsultaJaspar jasparRequest);
}
