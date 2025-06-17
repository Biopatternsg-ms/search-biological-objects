package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;

import java.util.List;

public interface BuildTranscriptionFactorsService {
    List<TranscriptionFactor> executeJaspar(JasparRequest jasparRequest);
    List<TranscriptionFactor> executeTFBind(PromoterRegionRequest promoterRegionRequest);
}
