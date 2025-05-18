package domain.port;

import infrastructure.dtos.PromoterRegionDTO;
import domain.models.TranscriptionFactor;

import java.util.List;

public interface TranscriptionFactorTFBindDataProviderPort {
    List<TranscriptionFactor> fetchDataFromTFBindSource(PromoterRegionDTO tfRequest);
}
