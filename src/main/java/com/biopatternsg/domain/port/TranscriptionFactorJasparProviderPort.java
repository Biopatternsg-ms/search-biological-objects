package com.biopatternsg.domain.port;

import com.biopatternsg.infrastructure.dtos.JasparTFRequest;
import com.biopatternsg.domain.models.TranscriptionFactor;

import java.util.List;

public interface TranscriptionFactorJasparProviderPort {
    List<TranscriptionFactor> fetchDataFromJasparSource(JasparTFRequest jasparRequest);
}
