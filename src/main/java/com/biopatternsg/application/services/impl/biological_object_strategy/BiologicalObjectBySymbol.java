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
package com.biopatternsg.application.services.impl.biological_object_strategy;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.domain.models.external_entities.HGNCResponse;
import com.biopatternsg.domain.models.external_entities.UniprotResponse;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.external_repositories.HGNCRepository;
import com.biopatternsg.domain.port.out.external_repositories.UniprotRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectBySymbol implements BuildBiologicalObjectStrategy, ChainResponsibility {

    private final HGNCRepository hgncRepository;
    private final UniprotRepository uniprotRepository;
    private ChainResponsibility next;

    @Override
    public BiologicalObject execute(String value) {

        var hgncResponse = hgncRepository.findSymbolInformation(value);
        var biologicalObject = formatHGNCInformation(hgncResponse);
        addUniprotInformation(biologicalObject, biologicalObject.getUniprotId());

        return biologicalObject;
    }

    private BiologicalObject formatHGNCInformation(HGNCResponse hgncResponse) {

        return BiologicalObject.builder()
                        .hgncId(hgncResponse.getId())
                        .symbol(hgncResponse.getSymbol())
                        .name(hgncResponse.getName())
                        .locusType(hgncResponse.getLocusType())
                        .ensemblGeneId(hgncResponse.getEnsemblGeneId())
                        .synonyms(hgncResponse.getSynonyms())
                        .uniprotId(hgncResponse.getUniprotId())
                        .geneFamilies(hgncResponse.getGeneFamilies())
                        .build();
    }

    private void addUniprotInformation(BiologicalObject biologicalObject, String uniprotId) {
        var uniprotResponse = uniprotRepository.findInfo(uniprotId);
        biologicalObject.getSynonyms().addAll(uniprotResponse.getSynonyms());
        addGOCodes(biologicalObject, uniprotResponse);
    }

    private void addGOCodes(BiologicalObject biologicalObject, UniprotResponse uniprotResponse) {
        GeneOntology ontologyLists = new GeneOntology();
        ontologyLists.setBiologicalProcess(uniprotResponse.getGoBp());
        ontologyLists.setMolecularFunction(uniprotResponse.getGoMf());
        ontologyLists.setCellularComponent(uniprotResponse.getGoCc());
        biologicalObject.setGeneOntology(ontologyLists);
    }

    @Override
    public void setNext(ChainResponsibility chainResponsibility) {
        this.next = chainResponsibility;
    }

    @Override
    public ChainResponsibility getNext() {
        return this.next;
    }

    @Override
    public BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig) {

        try{
            return execute(biologicalObjectConfig.getSymbol());
        } catch (Exception e) {
            return BiologicalObject.builder().symbol(biologicalObjectConfig.getSymbol()).build();
        }
    }
}
