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
import com.biopatternsg.infrastructure.internal_services.QueryIntegrations;
import com.biopatternsg.infrastructure.external_services.QueryPdb;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Data;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Participants;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Response;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PdbAdapter implements PdbRepository {

    private final QueryIntegrations queryIntegrations;
    private final QueryPdb queryPdbe;

    @Override
    public List<Complex> getComplexes(String uniprotId) {
        return queryIntegrations.getComplexes(uniprotId);
    }

    /**
     * @deprecated Usar {@link #getComplexes(String)} que obtiene los complejos con puntuación desde integrations.
     */
    @Deprecated(since = "2026")
    public List<Complex> getComplexesDirect(String uniprotId) {
        List<Data> dataList = getPdbResponse(uniprotId);
        return dataList.stream()
                .map(data -> {
                    String complexId = data.pdbComplexId();
                    List<String> participants = data.participants().stream()
                            .map(Participants::accession)
                            .filter(accession -> !accession.equals(uniprotId))
                            .toList();

                    return new Complex(complexId, participants, 0.0f);
                })
                .toList();
    }

    /**
     * @deprecated Método auxiliar de la consulta directa obsoleta.
     */
    @Deprecated(since = "2026")
    private List<Data> getPdbResponse(String uniprotId) {

        try{
            Response response = queryPdbe.search(uniprotId);
            return Optional.ofNullable(response)
                    .map(Response::uniprotIndex)
                    .map(map -> map.get(uniprotId))
                    .orElse(Collections.emptyList());

        } catch (Exception e) {
            log.info("Error con PDBe: {}", e.getMessage());
            return List.of();
        }
    }
}
