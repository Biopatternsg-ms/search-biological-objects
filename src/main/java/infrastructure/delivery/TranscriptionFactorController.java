package infrastructure.delivery;

import infrastructure.dtos.JasparRequest;
import infrastructure.dtos.PromoterRegionDTO;
import application.usecase.GetJasparTransciptionFactors;
import application.usecase.GetTFBindTranscriptionFactors;
import domain.models.TranscriptionFactor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
@Path("/transcription-factor")
public class TranscriptionFactorController {

    private final GetTFBindTranscriptionFactors getTFBindTranscriptionFactors;
    private final GetJasparTransciptionFactors getJasparTransciptionFactors;

    public TranscriptionFactorController(GetTFBindTranscriptionFactors getTFBindTranscriptionFactors, GetJasparTransciptionFactors getJasparTransciptionFactors) {
        this.getTFBindTranscriptionFactors = getTFBindTranscriptionFactors;
        this.getJasparTransciptionFactors = getJasparTransciptionFactors;
    }

    @POST
    @Path("/by-promoter-region")
    public List<TranscriptionFactor> getByPromoterRegion(PromoterRegionDTO promoterRegionDTO) {
        return getTFBindTranscriptionFactors.algo(promoterRegionDTO);
    }

    @POST
    @Path("/by-promoter-region-jaspar")
    public Response getByPromoterJasparRegion(
            @Valid PromoterRegionDTO promoterRegionDTO
    ) {
        return Response.ok(getJasparTransciptionFactors.getBlatSearchOptions(promoterRegionDTO)).build();
    }

    @POST
    @Path("/by-blat-coordinates")
    public List<TranscriptionFactor> getByPromoterJasparRegion(JasparRequest blastCoordinatesRequest) {
        return getJasparTransciptionFactors.esteSi(blastCoordinatesRequest);
    }

}
