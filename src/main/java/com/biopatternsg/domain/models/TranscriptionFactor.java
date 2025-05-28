package com.biopatternsg.domain.models;

import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.infrastructure.dtos.JasparRegionData;
import lombok.Builder;

@Builder
public record TranscriptionFactor(
        //String id,
        String name,
        float reliability,
        int number,
        String sign,
        String chain,
        TranscriptionFactorSource source
) {
    public static TranscriptionFactor of(
            JasparRegionData.JasparTranscriptionFactor jasparTranscriptionFactor,
            int maxScore
    ) {
        return TranscriptionFactor.builder()
                .name(jasparTranscriptionFactor.TFName())
                .reliability(jasparTranscriptionFactor.score() * ((float) 100 / maxScore))
                .source(TranscriptionFactorSource.JASPAR)
                .sign("(" + jasparTranscriptionFactor.strand() + ")")
                .build();
    }
}
