package com.biopatternsg.domain.port;

import com.biopatternsg.infrastructure.dtos.PromoterRegionDTO;
import com.biopatternsg.domain.models.TranscriptionFactor;

import java.util.List;

public interface TranscriptionFactorTFBindDataProviderPort {
    List<TranscriptionFactor> fetchDataFromTFBindSource(PromoterRegionDTO tfRequest);
}
