package application.usecase;

import infrastructure.dtos.PromoterRegionDTO;
import domain.models.TranscriptionFactor;
import domain.port.TranscriptionFactorTFBindDataProviderPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetTFBindTranscriptionFactors {

    private final TranscriptionFactorTFBindDataProviderPort externalDataProviderPort;

    public GetTFBindTranscriptionFactors(TranscriptionFactorTFBindDataProviderPort externalDataProviderPort) {
        this.externalDataProviderPort = externalDataProviderPort;
    }

    public List<TranscriptionFactor> algo(PromoterRegionDTO tfRequest) {
        return externalDataProviderPort.fetchDataFromTFBindSource(tfRequest);
    }

}
