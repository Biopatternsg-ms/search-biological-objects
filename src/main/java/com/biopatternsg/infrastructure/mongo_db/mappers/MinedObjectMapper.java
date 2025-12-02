package com.biopatternsg.infrastructure.mongo_db.mappers;

import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.infrastructure.mongo_db.collections.MinedObjectCollection;

public class MinedObjectMapper {

    public static MinedObject toMinedObject(MinedObjectCollection minedObjectCollection){

        return MinedObject.builder()
                .id(minedObjectCollection.id.toString())
                .userId(minedObjectCollection.getUserId())
                .pipelineId(minedObjectCollection.getPipelineId())
                .biologicalObjectId(minedObjectCollection.getBiologicalObjectId())
                .uniprotId(minedObjectCollection.getUniprotId())
                .uniprotIdFather(minedObjectCollection.getUniprotIdFather())
                .level(minedObjectCollection.getLevel())
                .build();
    }

    public static MinedObjectCollection toMinedObjectCollection(MinedObject minedObject){

        return MinedObjectCollection.builder()
                //TODO chequearID
                .userId(minedObject.getUserId())
                .pipelineId(minedObject.getPipelineId())
                .biologicalObjectId(minedObject.getBiologicalObjectId())
                .uniprotId(minedObject.getUniprotId())
                .uniprotIdFather(minedObject.getUniprotIdFather())
                .level(minedObject.getLevel())
                .build();
    }
}
