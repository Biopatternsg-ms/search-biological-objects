package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.BuildTranscriptionFactorsService;
import com.biopatternsg.domain.port.in.FindTranscriptionFactor;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetTranscriptionFactorsUseCase implements FindTranscriptionFactor {

    private final BuildTranscriptionFactorsService buildTranscriptionFactorsService;

    public GetTranscriptionFactorsUseCase(BuildTranscriptionFactorsService buildJasparTranscriptionFactorsService) {
        this.buildTranscriptionFactorsService = buildJasparTranscriptionFactorsService;
    }

    @Override
    public List<TranscriptionFactor> getJasparTranscriptionFactors(JasparRequest jasparRequest) {
        return buildTranscriptionFactorsService.executeJaspar(jasparRequest);
    }

    @Override
    public List<TranscriptionFactor> getTFBindTranscriptionFactors(PromoterRegionRequest promoterRegionRequest) {
        return buildTranscriptionFactorsService.executeTFBind(promoterRegionRequest);
    }
}
