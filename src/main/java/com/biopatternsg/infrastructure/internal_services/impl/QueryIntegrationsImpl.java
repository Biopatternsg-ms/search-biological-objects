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

import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.infrastructure.clients.internal_clients.IntegrationsHttpClient;
import com.biopatternsg.infrastructure.internal_services.QueryIntegrations;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class QueryIntegrationsImpl implements QueryIntegrations {

    @RestClient
    @Inject
    private final IntegrationsHttpClient integrationsHttpClient;

    public QueryIntegrationsImpl(@RestClient IntegrationsHttpClient integrationsHttpClient) {
        this.integrationsHttpClient = integrationsHttpClient;
    }

    @Override
    @Retry
    public List<Complex> getComplexes(String uniprotId) {
        return integrationsHttpClient.getComplexes(uniprotId);
    }
}
