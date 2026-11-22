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
package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.domain.models.JasparQuery;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.out.external_repositories.JasparRepository;
import com.biopatternsg.infrastructure.dtos.JasparRegion;
import com.biopatternsg.infrastructure.dtos.JasparRegionData;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.external_services.QueryJaspar;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@ApplicationScoped
public class JasparAdapter implements JasparRepository {

    private final QueryJaspar queryJaspar;

    public JasparAdapter(QueryJaspar queryJaspar) {
        this.queryJaspar = queryJaspar;
    }

    @Override
    public List<TranscriptionFactor> fetchDataFromJasparSource(JasparQuery jasparQuery) {

        JasparRequest jasparRequest = new JasparRequest(
                jasparQuery.genome(),
                jasparQuery.track(),
                jasparQuery.chromosome(),
                jasparQuery.start(),
                jasparQuery.end(),
                jasparQuery.strand(),
                jasparQuery.reliability()
        );

        JasparRegionData jasparRegionData = queryJaspar.getDataFromJasparSource(JasparRegion.of(jasparRequest));

        List<JasparRegionData.JasparTranscriptionFactor> jasparTranscriptionFactors = jasparRegionData.getTranscriptionFactors();
        int maxScore = Collections.max(jasparTranscriptionFactors, Comparator.comparingInt(JasparRegionData.JasparTranscriptionFactor::score)).score();

        float reliabilityScore = maxScore * ((float) jasparQuery.reliability() / 100);

        return jasparTranscriptionFactors.stream()
                .filter(jtf -> jtf.score() >= reliabilityScore)
                .map(jtf -> TranscriptionFactor.builder()
                        .name(jtf.TFName())
                        .reliability(jtf.score() * ((float) 100 / maxScore))
                        .source(TranscriptionFactorSource.JASPAR)
                        .sign("(" + jtf.strand() + ")")
                        .matrix(jtf.name())
                        .build())
                .toList();
    }
}
