package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.port.in.FindBlatOption;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/blat")
public class BlatController {

    private final FindBlatOption findBlatOptions;

    public BlatController(FindBlatOption findBlatOptions) {
        this.findBlatOptions = findBlatOptions;
    }

    @POST
    @Path("/search-options")
    public Response getByPromoterJasparRegion(
            @Valid PromoterRegionRequest promoterRegion) {
        return Response
                .ok(findBlatOptions.execute(promoterRegion))
                .build();
    }

}
