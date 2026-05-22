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
package com.biopatternsg.domain.models.external_entities;

import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
public record BlatSearchOptionsResponse(
        double identity,
        String chromosome,
        String strand,
        String start,
        String end
) {
    public static BlatSearchOptionsResponse of(String blatSearchOptionsString) {
        List<String> parts = Arrays.stream(blatSearchOptionsString.split(" ")).toList();

        return BlatSearchOptionsResponse.builder()
                .identity(Double.parseDouble(parts.get(5).replace("%", "")))
                .chromosome(parts.get(6))
                .strand(parts.get(7))
                .start(parts.get(8))
                .end(parts.get(9))
                .build();
    }
}
