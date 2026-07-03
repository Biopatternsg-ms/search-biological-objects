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
package com.biopatternsg.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class BiologicalObject {

    private String id;
    private String userId;
    private String symbol;
    private String name;
    private String locusType;
    private String ensemblGeneId;
    private String hgncId;
    private String uniprotId;
    private String meshId;
    private Set<String> synonyms;
    private GeneOntology geneOntology;
    private List<String> geneFamilies;
    private List<String> tissues;
    private TranscriptionFactor transcriptionFactor;

    public Set<String> getSynonyms() {
        if (this.synonyms == null) {
            this.synonyms = new java.util.HashSet<>();
        }
        if (this.symbol != null && !this.symbol.trim().isEmpty()) {
            this.synonyms.add(this.symbol);
        }
        if (this.name != null && !this.name.trim().isEmpty() && !this.synonyms.contains(this.name)) {
            this.synonyms.add(this.name);
        }
        return Set.copyOf(this.synonyms);
    }

    public void addSynonyms(java.util.Collection<String> newSynonyms) {
        if (newSynonyms == null || newSynonyms.isEmpty()) {
            return;
        }
        if (this.synonyms == null) {
            this.synonyms = new java.util.HashSet<>();
        }
        for (String newSynonym : newSynonyms) {
            if (newSynonym != null && !newSynonym.trim().isEmpty() && !containsIgnoreCase(newSynonym)) {
                this.synonyms.add(newSynonym);
            }
        }
    }

    private boolean containsIgnoreCase(String newSynonym) {
        return this.synonyms.stream().anyMatch(existing -> existing.equalsIgnoreCase(newSynonym));
    }
}
