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

import com.biopatternsg.infrastructure.clients.external_clients.HgncHttpClient;
import com.biopatternsg.infrastructure.external_services.QueryHGNC;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.FetchResponse;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.SearchResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class QueryHGNCImpl implements QueryHGNC {


    private final HgncHttpClient hgncHttpClient;

    public QueryHGNCImpl(@RestClient HgncHttpClient hgncHttpClient) {
        this.hgncHttpClient = hgncHttpClient;
    }

    @Override
    @Retry
    public SearchResponse search(String label) {
        return hgncHttpClient.search(label);
    }

    @Override
    @Retry
    public FetchResponse fetchSymbol(String symbol) {
        return hgncHttpClient.fetchSymbol(symbol);
    }

    @Override
    @Retry
    public FetchResponse fetchId(String hgncId) {
        return hgncHttpClient.fetchId(hgncId);
    }

    @Override
    @Retry
    public FetchResponse fetchUniprotId(String uniprotId) {
        return hgncHttpClient.fetchUniprotId(uniprotId);
    }
}
