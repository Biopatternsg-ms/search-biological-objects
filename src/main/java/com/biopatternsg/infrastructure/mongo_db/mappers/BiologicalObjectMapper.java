package com.biopatternsg.infrastructure.mongo_db.mappers;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class BiologicalObjectMapper {

    public static BiologicalObject toBiologicalObject(BiologicalObjectCollection biologicalObjectCollection) {

        if (biologicalObjectCollection == null) {
            return null;
        }
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

    public static BiologicalObjectCollection toBiologicalObjectCollection(BiologicalObject biologicalObject) {

        return BiologicalObjectCollection.builder()
                .userId(biologicalObject.getUserId())
                .hgncId(biologicalObject.getHgncId())
                .uniprotId(biologicalObject.getUniprotId())
                .meshId(biologicalObject.getMeshId())
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

    public static List<BiologicalObject> toBiologicalObjects(List<BiologicalObjectCollection> biologicalObjectCollections){
        return biologicalObjectCollections.stream().map(BiologicalObjectMapper::toBiologicalObject).toList();
    }
}
