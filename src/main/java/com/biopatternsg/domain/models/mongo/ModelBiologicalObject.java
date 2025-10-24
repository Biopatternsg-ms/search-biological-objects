package com.biopatternsg.domain.models.mongo;

import com.biopatternsg.domain.models.GeneOntology;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Builder
@MongoEntity(collection = "bioObjects")
public class ModelBiologicalObject extends PanacheMongoEntity {

    private String id;
    private String symbol;
    private String name;
    private String locusType;
    private String uniprotId;
    private Set<String> synonyms;
    private GeneOntology geneOntology;

    public ModelBiologicalObject() {

    }
}
