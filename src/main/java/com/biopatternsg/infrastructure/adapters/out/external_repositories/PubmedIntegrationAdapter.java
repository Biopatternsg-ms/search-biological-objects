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

import com.biopatternsg.domain.models.PaginatedResult;
import com.biopatternsg.domain.models.PipelineSynonym;
import com.biopatternsg.domain.port.out.external_repositories.PubmedIntegrationRepository;
import com.biopatternsg.infrastructure.internal_services.QueryPubmedIntegration;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class PubmedIntegrationAdapter implements PubmedIntegrationRepository {

    private final QueryPubmedIntegration queryPubmedIntegration;

    @Override
    public PaginatedResult<PipelineSynonym> getSynonyms(String pipelineId, int page, int size) {
        var dto = queryPubmedIntegration.getSynonyms(pipelineId, page, size);

        List<PipelineSynonym> items = dto.items().stream()
                .map(synDto -> new PipelineSynonym(synDto.name(), synDto.synonyms()))
                .collect(Collectors.toList());

        return new PaginatedResult<>(
                items,
                dto.totalItems(),
                dto.totalPages(),
                dto.currentPage(),
                dto.size()
        );
    }
}
