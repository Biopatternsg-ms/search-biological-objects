package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.port.in.FindTranscriptionFactor;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/transcription-factor")
public class TranscriptionFactorController {

    private final FindTranscriptionFactor findTranscriptionFactor;

    public TranscriptionFactorController(FindTranscriptionFactor findTranscriptionFactor) {
        this.findTranscriptionFactor = findTranscriptionFactor;
    }

    @POST
    @Path("/by-promoter-region")
    public Response getByPromoterRegion(
            @Valid PromoterRegionRequest promoterRegionRequest) {
        return Response
                .ok(findTranscriptionFactor.getTFBindTranscriptionFactors(promoterRegionRequest))
                .build();
    }

    @POST
    @Path("/by-blat-coordinates")
    public Response getByPromoterJasparRegion(JasparRequest jasparRequest) {
        return Response
                .ok(findTranscriptionFactor.getJasparTranscriptionFactors(jasparRequest))
                .build();
    }

}
