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

import com.biopatternsg.infrastructure.clients.external_clients.TFBindHttpClient;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.infrastructure.external_services.QueryTFBIND;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class QueryTFBINDImpl implements QueryTFBIND {

    private final TFBindHttpClient tfBindHttpClient;

    public QueryTFBINDImpl(@RestClient TFBindHttpClient tfBindHttpClient) {
        this.tfBindHttpClient = tfBindHttpClient;
    }

    @Override
    @Retry
    public String getByPromoterRegion(PromoterRegionRequest promoterRegion) {

        try{
            return tfBindHttpClient.getByPromoterRegion(">COMMENTS\n" + promoterRegion.promoterRegion());
        } catch (Exception e) {
            log.info("Error con tfbind: {}", e.getMessage());
            return "";
        }
    }
}
