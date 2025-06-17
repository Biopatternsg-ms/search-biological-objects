package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.domain.models.TranscriptionFactor;

import java.util.List;

public interface TFBindRepository {
    List<TranscriptionFactor> fetchDataFromTFBindSource(PromoterRegionRequest tfRequest);
}
