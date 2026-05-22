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

import jakarta.ws.rs.QueryParam;
import lombok.Builder;

@Builder
public class JasparRegion {
        @QueryParam("genome") public String genome;
        @QueryParam("track") public String track;
        @QueryParam("chrom") public String chrom;
        @QueryParam("start") public String start;
        @QueryParam("end") public String end;

        public static JasparRegion of(JasparRequest jasparRequest) {
                return JasparRegion.builder()
                        .genome(jasparRequest.genome())
                        .track(jasparRequest.track())
                        .chrom(jasparRequest.chromosome())
                        .start(jasparRequest.start())
                        .end(jasparRequest.end())
                        .build();
        }
}