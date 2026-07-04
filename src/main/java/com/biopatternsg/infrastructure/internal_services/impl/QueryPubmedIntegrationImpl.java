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
package com.biopatternsg.infrastructure.internal_services.impl;

import com.biopatternsg.infrastructure.clients.internal_clients.PubmedIntegrationHttpClient;
import com.biopatternsg.infrastructure.dtos.PaginatedResultDTO;
import com.biopatternsg.infrastructure.dtos.PipelineSynonymDTO;
import com.biopatternsg.infrastructure.internal_services.QueryPubmedIntegration;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class QueryPubmedIntegrationImpl implements QueryPubmedIntegration {

    private final PubmedIntegrationHttpClient pubmedIntegrationHttpClient;

    public QueryPubmedIntegrationImpl(@RestClient PubmedIntegrationHttpClient pubmedIntegrationHttpClient) {
        this.pubmedIntegrationHttpClient = pubmedIntegrationHttpClient;
    }

    @Override
    @Retry
    public PaginatedResultDTO<PipelineSynonymDTO> getSynonyms(String pipelineId, int page, int size) {
        return pubmedIntegrationHttpClient.getSynonyms(pipelineId, page, size);
    }
}
