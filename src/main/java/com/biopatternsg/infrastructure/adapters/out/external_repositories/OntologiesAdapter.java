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

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.domain.port.out.external_repositories.OntologiesRepository;
import com.biopatternsg.infrastructure.internal_services.QueryOntologies;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class OntologiesAdapter implements OntologiesRepository {

    private final QueryOntologies queryOntologies;

    @Override
    public void buildGeneOntologyTree(GeneOntology geneOntology) {
        if (geneOntology == null) {
            return;
        }
        try (Response response = queryOntologies.buildGeneOntologyTree(geneOntology)) {
            if (response != null && response.hasEntity()) {
                log.info(response.readEntity(String.class));
            }
        } catch (Exception e) {
            log.error("Error building gene ontology tree in adapter: {}", e.getMessage(), e);
        }
    }

    @Override
    public void buildMeshOntologyTree(BiologicalObject biologicalObject) {
        if (biologicalObject == null) {
            return;
        }
        try (Response response = queryOntologies.buildMeshOntologyTree(biologicalObject)) {
            if (response != null && response.hasEntity()) {
                log.info(response.readEntity(String.class));
            }
        } catch (Exception e) {
            log.error("Error building mesh ontology tree in adapter: {}", e.getMessage(), e);
        }
    }
}
