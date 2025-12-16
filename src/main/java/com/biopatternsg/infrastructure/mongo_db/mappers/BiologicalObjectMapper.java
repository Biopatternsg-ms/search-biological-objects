package com.biopatternsg.infrastructure.mongo_db.mappers;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BiologicalObjectMapper {
    
    public static BiologicalObject toBiologicalObject(BiologicalObjectCollection biologicalObjectCollection){

        return BiologicalObject.builder()
                .id(biologicalObjectCollection.id.toString())
                .userId(biologicalObjectCollection.getUserId())
                .hgncId(biologicalObjectCollection.getHgncId())
                .uniprotId(biologicalObjectCollection.getUniprotId())
                .symbol(biologicalObjectCollection.getSymbol())
                .name(biologicalObjectCollection.getName())
                .locusType(biologicalObjectCollection.getLocusType())
                .synonyms(biologicalObjectCollection.getSynonyms())
                .geneOntology(biologicalObjectCollection.getGeneOntology())
                .geneFamilies(biologicalObjectCollection.getGeneFamilies())
                .tissues(biologicalObjectCollection.getTissues())
                .transcriptionFactor(biologicalObjectCollection.getTranscriptionFactor())
                .build();
    }
    
    public static BiologicalObjectCollection toBiologicalObjectCollection(BiologicalObject biologicalObject){

        return BiologicalObjectCollection.builder()
                //TODO chequearID
                .userId(biologicalObject.getUserId())
                .hgncId(biologicalObject.getHgncId())
                .uniprotId(biologicalObject.getUniprotId())
                .symbol(biologicalObject.getSymbol())
                .name(biologicalObject.getName())
                .locusType(biologicalObject.getLocusType())
                .synonyms(biologicalObject.getSynonyms())
                .geneOntology(biologicalObject.getGeneOntology())
                .geneFamilies(biologicalObject.getGeneFamilies())
                .tissues(biologicalObject.getTissues())
                .transcriptionFactor(biologicalObject.getTranscriptionFactor())
                .build();
    }
}
