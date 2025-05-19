package infrastructure.delivery;

import infrastructure.dtos.JasparTFRequest;
import infrastructure.dtos.PromoterRegionDTO;
import application.usecase.GetJasparTranscriptionFactors;
import application.usecase.GetTFBindTranscriptionFactors;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/transcription-factor")
public class TranscriptionFactorController {

    private final GetTFBindTranscriptionFactors getTFBindTranscriptionFactors;
    private final GetJasparTranscriptionFactors getJasparTranscriptionFactors;

    public TranscriptionFactorController(GetTFBindTranscriptionFactors getTFBindTranscriptionFactors, GetJasparTranscriptionFactors getJasparTransciptionFactors) {
        this.getTFBindTranscriptionFactors = getTFBindTranscriptionFactors;
        this.getJasparTranscriptionFactors = getJasparTransciptionFactors;
    }

    @POST
    @Path("/by-promoter-region")
    public Response getByPromoterRegion(
            @Valid PromoterRegionDTO promoterRegionDTO) {
        return Response
                .ok(getTFBindTranscriptionFactors.getTFBindTranscriptionFactors(promoterRegionDTO))
                .build();
    }

    @POST
    @Path("/by-blat-coordinates")
    public Response getByPromoterJasparRegion(JasparTFRequest blatCoordinatesRequest) {
        return Response
                .ok(getJasparTranscriptionFactors.getJasparTranscriptionFactors(blatCoordinatesRequest))
                .build();
    }

}
