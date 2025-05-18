package domain.port;

import infrastructure.dtos.JasparRequest;
import infrastructure.dtos.PromoterRegionDTO;
import domain.models.BlatSearchOptions;
import domain.models.TranscriptionFactor;

import java.util.List;

public interface TranscriptionFactorJasparProviderPort {
    List<BlatSearchOptions> fetchDataFromBlatSource(PromoterRegionDTO promoterRegionDTO);
    List<TranscriptionFactor> fetchDataFromJasparSource(JasparRequest jasparRequest);
}
