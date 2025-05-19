package application.usecase;

import infrastructure.dtos.JasparTFRequest;
import domain.models.TranscriptionFactor;
import domain.port.TranscriptionFactorJasparProviderPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetJasparTranscriptionFactors {

    private final TranscriptionFactorJasparProviderPort transcriptionFactorJasparProviderPort;

    public GetJasparTranscriptionFactors(TranscriptionFactorJasparProviderPort transcriptionFactorJasparProviderPort) {
        this.transcriptionFactorJasparProviderPort = transcriptionFactorJasparProviderPort;
    }

    public List<TranscriptionFactor> getJasparTranscriptionFactors(JasparTFRequest findJasparTFRequest) {
        return transcriptionFactorJasparProviderPort.fetchDataFromJasparSource(findJasparTFRequest);
    }
}
