package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.in.FindTranscriptionFactor;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@ApplicationScoped
@Path("/transcription-factor")
@RequiredArgsConstructor
public class TranscriptionFactorController {

    private final FindTranscriptionFactor findTranscriptionFactor;

    @POST
    @Path("/by-promoter-region")
    @Operation(
        summary = "Find transcription factors by promoter region",
        description = "Retrieves a list of transcription factors that bind to the specified promoter region sequence."
    )
    @APIResponse(
        responseCode = "200",
        description = "Successfully retrieved transcription factors",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = TranscriptionFactor.class,
                type = SchemaType.ARRAY
            )
        )
    )
    public Response getByPromoterRegion(
            @Valid PromoterRegionRequest promoterRegionRequest) {
        return Response
                .ok(findTranscriptionFactor.getTFBindTranscriptionFactors(promoterRegionRequest))
                .build();
    }

    @POST
    @Path("/by-blat-coordinates")
    @Operation(
        summary = "Find transcription factors by BLAT coordinates",
        description = "Retrieves transcription factors based on genomic coordinates from a BLAT alignment."
    )
    @APIResponse(
        responseCode = "200",
        description = "Successfully retrieved transcription factors",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = TranscriptionFactor.class,
                type = SchemaType.ARRAY,
                description = "List of transcription factors for the specified genomic coordinates"
            )
        )
    )
    public Response getByPromoterJasparRegion(@Valid JasparRequest jasparRequest) {
        return Response
                .ok(findTranscriptionFactor.getJasparTranscriptionFactors(jasparRequest))
                .build();
    }

}
