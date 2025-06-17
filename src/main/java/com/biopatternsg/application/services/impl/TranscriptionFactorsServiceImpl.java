package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BuildTranscriptionFactorsService;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.out.external_repositories.JasparRepository;
import com.biopatternsg.domain.port.out.external_repositories.TFBindRepository;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class TranscriptionFactorsServiceImpl implements BuildTranscriptionFactorsService {

    private final JasparRepository jasparRepository;
    private final TFBindRepository tfBindRepository;

    @Override
    public List<TranscriptionFactor> executeJaspar(JasparRequest jasparRequest) {
        return jasparRepository.fetchDataFromJasparSource(jasparRequest);
    }

    @Override
    public List<TranscriptionFactor> executeTFBind(PromoterRegionRequest promoterRegionRequest) {
        return tfBindRepository.fetchDataFromTFBindSource(promoterRegionRequest);
    }
}
