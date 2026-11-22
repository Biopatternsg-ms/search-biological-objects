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

import com.biopatternsg.domain.models.external_entities.UniprotResponse;
import com.biopatternsg.infrastructure.external_services.QueryUniprot;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniprotAdapterTest {

    @Mock
    private QueryUniprot queryUniprot;

    private UniprotAdapter uniprotAdapter;

    @BeforeEach
    void setUp() {
        uniprotAdapter = new UniprotAdapter(queryUniprot);
    }

    @Test
    @DisplayName("Should successfully fetch and format UniProt entry")
    void shouldFindInfoSuccessfully() {
        String accession = "P04637";

        FullName recFullName = new FullName("Cellular tumor antigen p53");
        RecommendedName recName = new RecommendedName(recFullName);
        FullName altFullName = new FullName("Tumor suppressor p53");
        AlternativeName altName = new AlternativeName(altFullName);
        ProteinDescription proteinDescription = new ProteinDescription(recName, List.of(altName));

        GeneName geneName = new GeneName("TP53");
        Gene gene = new Gene(geneName, Collections.emptyList());

        Property propCc = new Property("GoEvidenceType", "C:nucleus");
        Property propMf = new Property("GoEvidenceType", "F:DNA-binding");
        Property propBp = new Property("GoEvidenceType", "P:apoptotic process");
        KBCrossReference refCc = new KBCrossReference("GO", "GO:0005634", List.of(propCc), Collections.emptyList());
        KBCrossReference refMf = new KBCrossReference("GO", "GO:0003677", List.of(propMf), Collections.emptyList());
        KBCrossReference refBp = new KBCrossReference("GO", "GO:0006915", List.of(propBp), Collections.emptyList());

        Response response = new Response(
                "UniProtKB reviewed",
                accession,
                Collections.emptyList(),
                "P53_HUMAN",
                proteinDescription,
                List.of(gene),
                List.of(refCc, refMf, refBp),
                null
        );

        when(queryUniprot.get(accession)).thenReturn(response);

        UniprotResponse result = uniprotAdapter.findInfo(accession);

        assertNotNull(result);
        assertEquals(accession, result.getId());
        assertEquals("Cellular tumor antigen p53", result.getName());
        assertEquals("TP53", result.getSymbol());
        assertTrue(result.getSynonyms().contains("Tumor suppressor p53"));
        assertTrue(result.getSynonyms().contains("Cellular tumor antigen p53"));
        assertTrue(result.getGoCc().contains("GO:0005634"));
        assertTrue(result.getGoMf().contains("GO:0003677"));
        assertTrue(result.getGoBp().contains("GO:0006915"));
        verify(queryUniprot, times(1)).get(accession);
        verify(queryUniprot, never()).search(anyString());
    }

    @Test
    @DisplayName("Should return null when ID is null or blank")
    void shouldReturnNullWhenIdIsBlankOrNull() {
        assertNull(uniprotAdapter.findInfo(null));
        assertNull(uniprotAdapter.findInfo(""));
        assertNull(uniprotAdapter.findInfo("   "));
        verify(queryUniprot, never()).get(anyString());
    }

    @Test
    @DisplayName("Should return null when queryUniprot returns null")
    void shouldReturnNullWhenQueryReturnsNull() {
        when(queryUniprot.get("UNKNOWN")).thenReturn(null);

        UniprotResponse result = uniprotAdapter.findInfo("UNKNOWN");

        assertNull(result);
        verify(queryUniprot, times(1)).get("UNKNOWN");
    }

    @Test
    @DisplayName("Should catch exception and return null on query failure")
    void shouldHandleExceptionGracefully() {
        when(queryUniprot.get("P04637")).thenThrow(new RuntimeException("UniProt unavailable"));

        UniprotResponse result = uniprotAdapter.findInfo("P04637");

        assertNull(result);
        verify(queryUniprot, times(1)).get("P04637");
    }

    @Test
    @DisplayName("Should format safely when optional fields are null or empty")
    void shouldHandleSparseResponseSafely() {
        Response sparseResponse = new Response(
                "UniProtKB reviewed",
                "P12345",
                null,
                "TEST_HUMAN",
                null,
                Collections.emptyList(),
                null,
                null
        );

        when(queryUniprot.get("P12345")).thenReturn(sparseResponse);

        UniprotResponse result = uniprotAdapter.findInfo("P12345");

        assertNotNull(result);
        assertEquals("P12345", result.getId());
        assertNull(result.getName());
        assertNull(result.getSymbol());
        assertTrue(result.getSynonyms().isEmpty());
        assertTrue(result.getGoCc().isEmpty());
        assertTrue(result.getGoMf().isEmpty());
        assertTrue(result.getGoBp().isEmpty());
    }
}
