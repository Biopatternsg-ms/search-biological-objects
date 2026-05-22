/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        String matrix,
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
                .matrix(jasparTranscriptionFactor.name())
                .build();
    }
}
