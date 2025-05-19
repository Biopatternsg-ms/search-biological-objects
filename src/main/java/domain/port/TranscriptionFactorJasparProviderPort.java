package domain.port;

import infrastructure.dtos.JasparTFRequest;
import domain.models.TranscriptionFactor;

import java.util.List;

public interface TranscriptionFactorJasparProviderPort {
    List<TranscriptionFactor> fetchDataFromJasparSource(JasparTFRequest jasparRequest);
}
