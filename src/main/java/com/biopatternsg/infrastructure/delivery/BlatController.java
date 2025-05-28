package com.biopatternsg.infrastructure.delivery;

import com.biopatternsg.application.usecase.GetBlatSearchOptions;
import com.biopatternsg.infrastructure.dtos.PromoterRegionDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/blat")
public class BlatController {

    private final GetBlatSearchOptions getBlatSearchOptions;

    public BlatController(GetBlatSearchOptions getBlatSearchOptions) {
        this.getBlatSearchOptions = getBlatSearchOptions;
    }

    @POST
    @Path("/search-options")
    public Response getByPromoterJasparRegion(
            @Valid PromoterRegionDTO promoterRegionDTO) {
        return Response
                .ok(getBlatSearchOptions.getBlatSearchOptions(promoterRegionDTO))
                .build();
    }

}
