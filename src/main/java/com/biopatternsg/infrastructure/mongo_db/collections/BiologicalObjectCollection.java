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
package com.biopatternsg.infrastructure.mongo_db.collections;

import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.domain.models.TranscriptionFactor;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@MongoEntity(collection = "biologicalObjects")
public class BiologicalObjectCollection extends PanacheMongoEntity {

    private String userId;
    private String symbol;
    private String name;
    private String locusType;
    private String hgncId;
    private String uniprotId;
    private String meshId;
    private Set<String> synonyms;
    private GeneOntology geneOntology;
    private List<String> geneFamilies;
    private List<String> tissues;
    private TranscriptionFactor transcriptionFactor;
}
