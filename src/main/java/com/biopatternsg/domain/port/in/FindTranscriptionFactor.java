package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;

import java.util.List;

public interface FindTranscriptionFactor {
    List<TranscriptionFactor> getJasparTranscriptionFactors(JasparRequest jasparRequest);
    List<TranscriptionFactor> getTFBindTranscriptionFactors(PromoterRegionRequest promoterRegionRequest);
}
