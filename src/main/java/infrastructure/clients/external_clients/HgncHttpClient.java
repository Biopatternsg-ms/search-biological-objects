package infrastructure.clients.external_clients;

import infrastructure.external_services.dtos.hgnc.HGNCGeneInformation;
import infrastructure.external_services.dtos.hgnc.HGNCSymbol;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "hgnc-api")
public interface HgncHttpClient {

    @GET
    @Path("search/{label}")
    HGNCSymbol search(@PathParam("label") String label);

    @GET
    @Path("search/{symbol}")
    HGNCGeneInformation fetch(@PathParam("symbol") String symbol);

}
