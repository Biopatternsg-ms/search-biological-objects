package domain.models;

import domain.enums.TranscriptionFactorSource;
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
}
