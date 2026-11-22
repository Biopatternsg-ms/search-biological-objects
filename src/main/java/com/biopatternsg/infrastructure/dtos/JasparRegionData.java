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
package com.biopatternsg.infrastructure.dtos;

import java.util.List;

public record JasparRegionData(
        String downloadTime,
        long downloadTimeStamp,
        String genome,
        String trackType,
        String track,
        String chrom,
        long chromSize,
        String bigDataUrl,
        long start,
        long end,
        List<JasparTranscriptionFactor> jaspar2022,
        int itemsReturned
) {
    public record JasparTranscriptionFactor(
            String chrom,
            int chromStart,
            int chromEnd,
            String name,
            int score,
            String strand,
            String TFName
    ) {}

    public List<JasparTranscriptionFactor> getTranscriptionFactors() { return jaspar2022; }
}
