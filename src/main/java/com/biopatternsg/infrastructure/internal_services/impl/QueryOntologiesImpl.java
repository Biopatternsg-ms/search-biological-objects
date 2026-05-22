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

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.infrastructure.clients.internal_clients.OntologiesHttpClient;
import com.biopatternsg.infrastructure.internal_services.QueryOntologies;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class QueryOntologiesImpl implements QueryOntologies {

    private final OntologiesHttpClient ontologiesHttpClient;

    public QueryOntologiesImpl(@RestClient OntologiesHttpClient ontologiesHttpClient) {
        this.ontologiesHttpClient = ontologiesHttpClient;
    }

    @Override
    @Retry
    public Response buildGeneOntologyTree(GeneOntology geneOntology) {
        try {
            return ontologiesHttpClient.buildGeneOntologyTree(geneOntology);
        } catch (Exception e) {
            log.error("Error building gene ontology tree: {}", e.getMessage());
            return Response.serverError().build();
        }
    }

    @Override
    public Response buildMeshOntologyTree(BiologicalObject biologicalObject) {
        try {
            return ontologiesHttpClient.buildMeshOntologyTree(biologicalObject);
        } catch (Exception e) {
            log.error("Error building mesh ontology tree: {}", e.getMessage());
            return Response.serverError().build();
        }
    }
}
