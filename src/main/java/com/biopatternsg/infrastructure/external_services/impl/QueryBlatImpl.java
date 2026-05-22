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

import com.biopatternsg.infrastructure.clients.external_clients.BlatHttpClient;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.infrastructure.external_services.QueryBlat;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class QueryBlatImpl implements QueryBlat {

    private final BlatHttpClient blatHttpClient;

    public QueryBlatImpl(@RestClient BlatHttpClient blatHttpClient) {
        this.blatHttpClient = blatHttpClient;
    }

    @Override
    @Retry
    public String getByPromoterJasparRegion(PromoterRegionRequest promoterRegionDTO) {
        return blatHttpClient.getByPromoterRegion("BLAT%27s+guess", promoterRegionDTO.promoterRegion());
    }
}
