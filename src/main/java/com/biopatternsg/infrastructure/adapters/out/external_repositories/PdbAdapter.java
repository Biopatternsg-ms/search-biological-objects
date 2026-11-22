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

import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import com.biopatternsg.infrastructure.clients.external_clients.PdbeHttpClient;
import com.biopatternsg.infrastructure.clients.model.pdb_complex.Assembly;
import com.biopatternsg.infrastructure.clients.model.pdb_complex.Data;
import com.biopatternsg.infrastructure.clients.model.pdb_complex.Participants;
import com.biopatternsg.infrastructure.clients.model.pdb_complex.Response;
import com.biopatternsg.infrastructure.external_services.QueryPdb;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class PdbAdapter implements PdbRepository {

    @ConfigProperty(name = "pdbe.target-organism", defaultValue = "Homo sapiens")
    String targetOrganism;

    private final QueryPdb queryPdbe;
    private final PdbeHttpClient pdbeHttpClient;

    @Inject
    public PdbAdapter(QueryPdb queryPdbe, @RestClient PdbeHttpClient pdbeHttpClient) {
        this.queryPdbe = queryPdbe;
        this.pdbeHttpClient = pdbeHttpClient;
    }

    @Override
    public List<Complex> getComplexes(String uniprotId) {
        try {
            Response response = pdbeHttpClient.search(uniprotId);

            List<Data> dataList = Optional.ofNullable(response)
                    .map(Response::uniprotIndex)
                    .map(map -> map.get(uniprotId))
                    .orElse(Collections.emptyList());

            return dataList.stream()
                    .sorted(Comparator.comparingDouble(this::calculateScore).reversed())
                    .map(data -> {
                        String complexId = data.pdbComplexId();
                        List<String> participants = data.participants().stream()
                                .map(Participants::accession)
                                .filter(accession -> !accession.equals(uniprotId))
                                .toList();

                        return Complex.builder()
                                .complexPdbId(complexId)
                                .participants(participants)
                                .score((float) calculateScore(data))
                                .build();
                    })
                    .toList();

        } catch (Exception e) {
            log.info("Error con PDBe en integrations: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private double calculateScore(Data data) {
        double score = 0;
        score += getComplexPortalScore(data);
        score += getOrganismScore(data);
        score += getAssembliesScore(data.assemblies());
        return score;
    }

    private double getComplexPortalScore(Data data) {
        return data.complexPortalId() != null ? 100.0 : 0.0;
    }

    private double getOrganismScore(Data data) {
        if (data.sourceOrganism() != null && targetOrganism != null && data.sourceOrganism().equalsIgnoreCase(targetOrganism)) {
            return 30.0;
        }
        return 0.0;
    }

    private double getAssembliesScore(List<Assembly> assemblies) {
        if (assemblies == null) {
            return 0.0;
        }
        double score = 0;
        for (Assembly assembly : assemblies) {
            score += getPreferredAssemblyScore(assembly);
            score += getResolutionScore(assembly);
            score += getMethodScore(assembly);
        }
        return score;
    }

    private double getPreferredAssemblyScore(Assembly assembly) {
        return Boolean.TRUE.equals(assembly.preferredAssembly()) ? 10.0 : 2.0;
    }

    private double getResolutionScore(Assembly assembly) {
        if (assembly.resolution() != null && assembly.resolution() > 0) {
            return Math.max(0.0, 5.0 - assembly.resolution()) * 2;
        }
        return 0.0;
    }

    private double getMethodScore(Assembly assembly) {
        if (assembly.experimentalMethod() != null) {
            String methodLower = assembly.experimentalMethod().toLowerCase();
            if (methodLower.contains("diffraction") || methodLower.contains("microscopy")) {
                return 5.0;
            }
            if (methodLower.contains("nmr") || methodLower.contains("magnetic resonance")) {
                return 2.0;
            }
        }
        return 0.0;
    }

    /**
     * @deprecated Use the new scoring search instead
     */
    @Deprecated(since = "2026-07-14", forRemoval = false)
    public List<Complex> getComplexesDeprecated(String uniprotId) {
        List<com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Data> dataList = getPdbResponseDeprecated(uniprotId);
        return dataList.stream()
                .map(data -> {
                    String complexId = data.pdbComplexId();
                    List<String> participants = data.participants().stream()
                            .map(com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Participants::accession)
                            .filter(accession -> !accession.equals(uniprotId))
                            .toList();

                    return Complex.builder()
                            .complexPdbId(complexId)
                            .participants(participants)
                            .score(0.0f)
                            .build();
                })
                .toList();
    }

    @Deprecated(since = "2026-07-14", forRemoval = false)
    private List<com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Data> getPdbResponseDeprecated(String uniprotId) {
        try {
            com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Response response = queryPdbe.search(uniprotId);
            return Optional.ofNullable(response)
                    .map(com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Response::uniprotIndex)
                    .map(map -> map.get(uniprotId))
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            log.info("Error con PDBe (Deprecated): {}", e.getMessage());
            return List.of();
        }
    }
}
