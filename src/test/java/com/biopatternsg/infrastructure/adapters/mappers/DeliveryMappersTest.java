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
package com.biopatternsg.infrastructure.adapters.mappers;

import com.biopatternsg.domain.enums.Genome;
import com.biopatternsg.domain.enums.Strand;
import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.models.JasparQuery;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectConfigDTO;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDetailDTO;
import com.biopatternsg.infrastructure.adapters.dtos.BlatSearchOptionDTO;
import com.biopatternsg.infrastructure.adapters.dtos.ComplexDTO;
import com.biopatternsg.infrastructure.adapters.dtos.LaunchPipelineRequest;
import com.biopatternsg.infrastructure.adapters.dtos.TranscriptionFactorConfigRequest;
import com.biopatternsg.infrastructure.adapters.dtos.TranscriptionFactorDTO;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryMappersTest {

    @Test
    @DisplayName("Should handle null inputs safely across all static delivery mappers")
    void shouldHandleNullInputsSafely() {
        assertNull(BiologicalObjectDtoMapper.toDetailDto(null));
        assertNull(BiologicalObjectDtoMapper.toSummaryDto(null));
        assertTrue(BiologicalObjectDtoMapper.toSummaryDtoList(null).isEmpty());

        assertNull(PipelineDtoMapper.toDomain(null));

        assertNull(TranscriptionFactorMapper.requestToModel(null));
        assertNull(TranscriptionFactorMapper.toDomain(null));
        assertNull(TranscriptionFactorMapper.toDto(null));
        assertTrue(TranscriptionFactorMapper.toDtoList(null).isEmpty());

        assertNull(ComplexDtoMapper.toDto(null));
        assertTrue(ComplexDtoMapper.toDtoList(null).isEmpty());

        assertNull(BlatDtoMapper.toDto(null));
        assertTrue(BlatDtoMapper.toDtoList(null).isEmpty());
    }

    @Test
    @DisplayName("Should map JasparRequest to JasparQuery and TranscriptionFactor to DTO")
    void shouldMapTranscriptionFactorModels() {
        JasparRequest request = new JasparRequest(
                "hg38", "jaspar2024", "chr17",
                "100", "200",
                "+", 800
        );
        JasparQuery query = TranscriptionFactorMapper.requestToModel(request);
        assertNotNull(query);
        assertEquals("hg38", query.genome());
        assertEquals("chr17", query.chromosome());
        assertEquals(800, query.reliability());

        TranscriptionFactor tf = TranscriptionFactor.builder()
                .name("TP53")
                .reliability(0.95f)
                .number(1)
                .sign("+")
                .chain("A")
                .matrix("MA0106.3")
                .source(TranscriptionFactorSource.JASPAR)
                .build();
        List<TranscriptionFactorDTO> dtos = TranscriptionFactorMapper.toDtoList(List.of(tf));
        assertEquals(1, dtos.size());
        assertEquals("TP53", dtos.getFirst().name());
        assertEquals(TranscriptionFactorSource.JASPAR, dtos.getFirst().source());
    }

    @Test
    @DisplayName("Should map LaunchPipelineRequest to PipelineConfig")
    void shouldMapPipelineRequest() {
        LaunchPipelineRequest request = new LaunchPipelineRequest(
                "pipe-1",
                3,
                10,
                List.of(new BiologicalObjectConfigDTO("P04637", "HGNC:11998", "TP53")),
                new TranscriptionFactorConfigRequest("ACTG", 85, BigDecimal.ONE, BigDecimal.TEN, Genome.HG38, "track", 0.9f, "chr1", Strand.POSITIVE, List.of(TranscriptionFactorSource.JASPAR))
        );

        PipelineConfig config = PipelineDtoMapper.toDomain(request);
        assertNotNull(config);
        assertEquals("pipe-1", config.getPipelineId());
        assertEquals(3, config.getLevels());
        assertEquals(1, config.getExpertObjects().size());
        assertEquals("TP53", config.getExpertObjects().getFirst().getSymbol());
        assertNotNull(config.getTranscriptionFactorConfig());
    }

    @Test
    @DisplayName("Should map BiologicalObject, Complex, and BlatSearchOptionsResponse to DTOs")
    void shouldMapOtherDeliveryDtos() {
        BiologicalObject bio = BiologicalObject.builder().id("b1").symbol("TP53").name("p53").build();
        BiologicalObjectDetailDTO detail = BiologicalObjectDtoMapper.toDetailDto(bio);
        assertNotNull(detail);
        assertEquals("b1", detail.id());

        Complex complex = Complex.builder().complexPdbId("1TUP").participants(List.of("P04637")).score(0.99f).build();
        List<ComplexDTO> complexes = ComplexDtoMapper.toDtoList(List.of(complex));
        assertEquals(1, complexes.size());
        assertEquals("1TUP", complexes.getFirst().complexPdbId());

        BlatSearchOptionsResponse blat = new BlatSearchOptionsResponse(99.5, "chr17", "+", "7661779", "7687550");
        List<BlatSearchOptionDTO> blatDtos = BlatDtoMapper.toDtoList(List.of(blat));
        assertEquals(1, blatDtos.size());
        assertEquals(99.5, blatDtos.getFirst().identity());
    }
}
