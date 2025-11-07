package com.biopatternsg.infrastructure.model_mongo;

import com.biopatternsg.domain.models.GeneOntology;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
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
@MongoEntity(collection = "biologicalObjects")
public class BiologicalObjectCollection extends PanacheMongoEntity {

    private Long userId;
    private String symbol;
    private String name;
    private String locusType;
    private String hgncId;
    private String uniprotId;
    private Set<String> synonyms;
    private GeneOntology geneOntology;
    private List<String> geneFamilies;
    private List<String> tissues;

    public BiologicalObjectCollection() {

    }
}
