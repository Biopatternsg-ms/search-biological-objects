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
package com.biopatternsg.infrastructure.external_services.impl;

import com.biopatternsg.infrastructure.clients.external_clients.UniprotHttpClient;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.ListResponse;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryUniprotImplTest {

    @Mock
    private UniprotHttpClient uniprotHttpClient;

    private QueryUniprotImpl queryUniprot;

    @BeforeEach
    void setUp() {
        queryUniprot = new QueryUniprotImpl(uniprotHttpClient);
    }

    @Test
    @DisplayName("Should delegate search to client")
    void shouldDelegateSearch() {
        ListResponse mockResponse = new ListResponse(Collections.emptyList());
        when(uniprotHttpClient.search("TP53", UniprotHttpClient.DEFAULT_SORT)).thenReturn(mockResponse);

        ListResponse result = queryUniprot.search("TP53");

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(uniprotHttpClient, times(1)).search("TP53", UniprotHttpClient.DEFAULT_SORT);
    }

    @Test
    @DisplayName("Should return empty list on blank search")
    void shouldReturnEmptyOnBlankSearch() {
        ListResponse resultNull = queryUniprot.search(null);
        ListResponse resultBlank = queryUniprot.search("   ");

        assertNotNull(resultNull);
        assertTrue(resultNull.results().isEmpty());
        assertNotNull(resultBlank);
        assertTrue(resultBlank.results().isEmpty());
        verify(uniprotHttpClient, never()).search(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return empty results when search returns 404 or 400")
    void shouldHandleSearch404() {
        when(uniprotHttpClient.search("NOTFOUND", UniprotHttpClient.DEFAULT_SORT))
                .thenThrow(new WebApplicationException(Status.NOT_FOUND));

        ListResponse result = queryUniprot.search("NOTFOUND");

        assertNotNull(result);
        assertTrue(result.results().isEmpty());
    }

    @Test
    @DisplayName("Should delegate get to client")
    void shouldDelegateGet() {
        Response mockResponse = new Response("reviewed", "P04637", null, "P53_HUMAN", null, null, null, null);
        when(uniprotHttpClient.get("P04637")).thenReturn(mockResponse);

        Response result = queryUniprot.get("P04637");

        assertNotNull(result);
        assertEquals("P04637", result.primaryAccession());
        verify(uniprotHttpClient, times(1)).get("P04637");
    }

    @Test
    @DisplayName("Should return null on blank or null get id")
    void shouldReturnNullOnBlankGet() {
        assertNull(queryUniprot.get(null));
        assertNull(queryUniprot.get("   "));
        verify(uniprotHttpClient, never()).get(anyString());
    }

    @Test
    @DisplayName("Should return null on 404 or 400 in get")
    void shouldReturnNullOn404Or400InGet() {
        when(uniprotHttpClient.get("UNKNOWN")).thenThrow(new WebApplicationException(Status.NOT_FOUND));
        when(uniprotHttpClient.get("INVALID")).thenThrow(new WebApplicationException(Status.BAD_REQUEST));

        assertNull(queryUniprot.get("UNKNOWN"));
        assertNull(queryUniprot.get("INVALID"));
    }

    @Test
    @DisplayName("Should rethrow 5xx errors in get")
    void shouldRethrow5xxInGet() {
        when(uniprotHttpClient.get("SERVER_ERROR")).thenThrow(new WebApplicationException(Status.INTERNAL_SERVER_ERROR));

        assertThrows(WebApplicationException.class, () -> queryUniprot.get("SERVER_ERROR"));
    }
}
