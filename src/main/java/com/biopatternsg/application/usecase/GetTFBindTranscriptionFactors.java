package com.biopatternsg.application.usecase;

import com.biopatternsg.infrastructure.dtos.PromoterRegionDTO;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.TranscriptionFactorTFBindDataProviderPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetTFBindTranscriptionFactors {

    private final TranscriptionFactorTFBindDataProviderPort transcriptionFactorTFBindDataPort;

    public GetTFBindTranscriptionFactors(TranscriptionFactorTFBindDataProviderPort transcriptionFactorTFBindDataPort) {
        this.transcriptionFactorTFBindDataPort = transcriptionFactorTFBindDataPort;
    }

    public List<TranscriptionFactor> getTFBindTranscriptionFactors(PromoterRegionDTO tfRequest) {
        return transcriptionFactorTFBindDataPort.fetchDataFromTFBindSource(tfRequest);
    }

}
