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
package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.OntologiesService;
import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.enums.Genome;
import com.biopatternsg.domain.enums.Strand;
import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.models.pipeline_config.TranscriptionFactorConfig;
import com.biopatternsg.domain.port.out.external_repositories.JasparRepository;
import com.biopatternsg.domain.port.out.external_repositories.TFBindRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptionFactorsServiceImplTest {

    @Mock
    private JasparRepository jasparRepository;

    @Mock
    private TFBindRepository tfBindRepository;

    @Mock
    private BiologicalObjectSearch biologicalObjectSearch;

    @Mock
    private BiologicalObjectRepository biologicalObjectRepository;

    @Mock
    private OntologiesService ontologiesService;

    @Mock
    private UserRepository userRepository;

    private TranscriptionFactorsServiceImpl transcriptionFactorsService;

    @BeforeEach
    void setUp() {
        transcriptionFactorsService = new TranscriptionFactorsServiceImpl(
                jasparRepository,
                tfBindRepository,
                biologicalObjectSearch,
                biologicalObjectRepository,
                ontologiesService,
                userRepository
        );
    }

    @Test
    @DisplayName("Should handle duplicate keys from JASPAR and keep the one with higher reliability")
    void shouldHandleDuplicateKeysFromJaspar() {
        TranscriptionFactor tf1 = TranscriptionFactor.builder()
                .name("ZNF384")
                .reliability(91.61491f)
                .number(0)
                .sign("(+)")
                .matrix("MA1125.1")
                .source(TranscriptionFactorSource.JASPAR)
                .build();

        TranscriptionFactor tf2 = TranscriptionFactor.builder()
                .name("ZNF384")
                .reliability(100.0f)
                .number(0)
                .sign("(+)")
                .matrix("MA1125.1")
                .source(TranscriptionFactorSource.JASPAR)
                .build();

        when(jasparRepository.fetchDataFromJasparSource(any())).thenReturn(List.of(tf1, tf2));

        TranscriptionFactorConfig config = TranscriptionFactorConfig.builder()
                .genome(Genome.HG38)
                .track("jaspar")
                .chromosome("chr1")
                .start(BigDecimal.valueOf(1000))
                .end(BigDecimal.valueOf(2000))
                .strand(Strand.POSITIVE)
                .reliability(80)
                .sources(List.of(TranscriptionFactorSource.JASPAR))
                .build();

        List<TranscriptionFactor> result = transcriptionFactorsService.executeGetTranscriptionFactors(config);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ZNF384", result.get(0).name());
        assertEquals(100.0f, result.get(0).reliability());
    }

    @Test
    @DisplayName("Should handle duplicates from TFBind and keep the one with higher reliability")
    void shouldHandleDuplicateKeysFromTfBind() {
        TranscriptionFactor tf1 = TranscriptionFactor.builder()
                .name("SP1")
                .reliability(85.0f)
                .source(TranscriptionFactorSource.TFBIND)
                .build();

        TranscriptionFactor tf2 = TranscriptionFactor.builder()
                .name("SP1")
                .reliability(95.0f)
                .source(TranscriptionFactorSource.TFBIND)
                .build();

        when(tfBindRepository.fetchDataFromTFBindSource(any(Integer.class), any())).thenReturn(List.of(tf1, tf2));

        TranscriptionFactorConfig config = TranscriptionFactorConfig.builder()
                .reliability(80)
                .promoterRegion("ATCG")
                .sources(List.of(TranscriptionFactorSource.TFBIND))
                .build();

        List<TranscriptionFactor> result = transcriptionFactorsService.executeGetTranscriptionFactors(config);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SP1", result.get(0).name());
        assertEquals(95.0f, result.get(0).reliability());
    }
}
