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

import com.biopatternsg.domain.models.external_entities.UniprotResponse;
import com.biopatternsg.domain.port.out.external_repositories.UniprotRepository;
import com.biopatternsg.infrastructure.external_services.QueryUniprot;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.*;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UniprotAdapter implements UniprotRepository {

    private final QueryUniprot queryUniprot;
    private static final int FIRST_VALUE = 0;

    @Override
    public UniprotResponse findInfo(String uniprotIds) {

        var response = queryUniprot.search(uniprotIds);
        if(response.results().isEmpty()){
            return null;
        }
        return formatUniprotInformation(response.results().getFirst());
    }

    private UniprotResponse formatUniprotInformation(Response response){

        var uniprotResponse = UniprotResponse.builder()
                .id(response.primaryAccession())
                .build();

        setName(uniprotResponse, response.proteinDescription().recommendedName());
        setGeneName(uniprotResponse, response.genes());
        setCodesGO(uniprotResponse, response.uniProtKBCrossReferences());
        setSynonyms(uniprotResponse, response.proteinDescription());

        return uniprotResponse;
    }

    private static void setCodesGO(UniprotResponse response, List<KBCrossReference> references){

        List<String> goCc = new ArrayList<>();
        List<String> goMf = new ArrayList<>();
        List<String> goBp = new ArrayList<>();

        references.forEach(reference -> reference.properties().forEach(property -> {
            String branch = property.value().split(":")[FIRST_VALUE];
            switch (branch) {
                case "C" -> goCc.add(reference.id());
                case "F" -> goMf.add(reference.id());
                case "P" -> goBp.add(reference.id());
            }
        }));

        response.setGoCc(goCc);
        response.setGoMf(goMf);
        response.setGoBp(goBp);
    }

    private static void setSynonyms(UniprotResponse response, ProteinDescription descriptions){

        Set<String> synonyms = new HashSet<>();
        if(descriptions.alternativeNames() != null){
            descriptions.alternativeNames().forEach(names -> synonyms.add(names.fullName().value()));
            synonyms.add(descriptions.recommendedName().fullName().value());
        }
        response.setSynonyms(synonyms);
    }

    private static void setGeneName(UniprotResponse response, List<Gene> gene){

        if(gene != null && gene.getFirst().geneName() != null){
            response.setSymbol(gene.getFirst().geneName().value());
        }
    }

    private static void setName(UniprotResponse response, RecommendedName recommendedName){

        if(recommendedName != null){
            response.setName(recommendedName.fullName().value());
        }
    }
}
