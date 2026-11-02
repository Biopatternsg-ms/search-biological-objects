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
import com.biopatternsg.infrastructure.external_services.QueryUniprot;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.ListResponse;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Collections;

@Slf4j
@ApplicationScoped
public class QueryUniprotImpl implements QueryUniprot {

    private final UniprotHttpClient uniprotHttpClient;

    public QueryUniprotImpl(@RestClient UniprotHttpClient uniprotHttpClient) {
        this.uniprotHttpClient = uniprotHttpClient;
    }

    @Override
    @Retry
    public ListResponse search(String label) {
        if (label == null || label.isBlank()) {
            return new ListResponse(Collections.emptyList());
        }
        try {
            return uniprotHttpClient.search(label, UniprotHttpClient.DEFAULT_SORT);
        } catch (WebApplicationException e) {
            int status = e.getResponse() != null ? e.getResponse().getStatus() : -1;
            if (status >= 400 && status < 500) {
                log.warn("UniProt search returned client error for '{}': status {}", label, status);
                return new ListResponse(Collections.emptyList());
            }
            log.error("UniProt search server error for '{}': status {}", label, status);
            throw e;
        }
    }

    @Override
    @Retry
    public Response get(String uniprotId) {
        if (uniprotId == null || uniprotId.isBlank()) {
            return null;
        }
        try {
            return uniprotHttpClient.get(uniprotId);
        } catch (WebApplicationException e) {
            int status = e.getResponse() != null ? e.getResponse().getStatus() : -1;
            if (status == 404 || status == 400) {
                log.warn("UniProt entry not found for ID '{}' (status {})", uniprotId, status);
                return null;
            }
            log.error("UniProt API server error for ID '{}': status {}", uniprotId, status);
            throw e;
        }
    }
}
