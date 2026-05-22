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
package com.biopatternsg.infrastructure.clients.external_clients;

import com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.FetchResponse;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.SearchResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "hgnc-api")
public interface HgncHttpClient {

    @GET
    @Path("search/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    SearchResponse search(@PathParam("label") String label);

    @GET
    @Path("fetch/symbol/{symbol}")
    @Produces(MediaType.APPLICATION_JSON)
    FetchResponse fetchSymbol(@PathParam("symbol") String symbol);

    @GET
    @Path("fetch/hgnc_id/{hgncId}")
    @Produces(MediaType.APPLICATION_JSON)
    FetchResponse fetchId(@PathParam("hgncId") String symbol);

    @GET
    @Path("fetch/uniprot_ids/{uniprotId}")
    @Produces(MediaType.APPLICATION_JSON)
    FetchResponse fetchUniprotId(@PathParam("uniprotId") String uniprotId);
}

