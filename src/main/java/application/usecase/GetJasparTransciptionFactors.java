package application.usecase;

import infrastructure.dtos.JasparRequest;
import infrastructure.dtos.PromoterRegionDTO;
import domain.models.BlatSearchOptions;
import domain.models.TranscriptionFactor;
import domain.port.TranscriptionFactorJasparProviderPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetJasparTransciptionFactors {

    private final TranscriptionFactorJasparProviderPort transcriptionFactorJasparProviderPort;

    public GetJasparTransciptionFactors(TranscriptionFactorJasparProviderPort transcriptionFactorJasparProviderPort) {
        this.transcriptionFactorJasparProviderPort = transcriptionFactorJasparProviderPort;
    }

    public List<BlatSearchOptions> getBlatSearchOptions(PromoterRegionDTO tfRequest) {
        return transcriptionFactorJasparProviderPort.fetchDataFromBlatSource(tfRequest);
    }

    public List<TranscriptionFactor> esteSi(JasparRequest jasparRequest) {
        return transcriptionFactorJasparProviderPort.fetchDataFromJasparSource(jasparRequest);
    }
}
