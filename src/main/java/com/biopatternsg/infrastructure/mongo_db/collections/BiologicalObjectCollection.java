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

    private Long userId;
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
