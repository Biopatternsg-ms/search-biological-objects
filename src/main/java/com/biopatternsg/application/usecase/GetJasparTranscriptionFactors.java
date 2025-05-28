package com.biopatternsg.application.usecase;

import com.biopatternsg.infrastructure.dtos.JasparTFRequest;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.TranscriptionFactorJasparProviderPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetJasparTranscriptionFactors {

    private final TranscriptionFactorJasparProviderPort transcriptionFactorJasparProviderPort;

    public GetJasparTranscriptionFactors(TranscriptionFactorJasparProviderPort transcriptionFactorJasparProviderPort) {
        this.transcriptionFactorJasparProviderPort = transcriptionFactorJasparProviderPort;
    }

    public List<TranscriptionFactor> getJasparTranscriptionFactors(JasparTFRequest findJasparTFRequest) {
        return transcriptionFactorJasparProviderPort.fetchDataFromJasparSource(findJasparTFRequest);
    }
}
