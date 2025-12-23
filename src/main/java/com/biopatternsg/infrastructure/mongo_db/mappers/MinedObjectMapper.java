package com.biopatternsg.infrastructure.mongo_db.mappers;

import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.infrastructure.mongo_db.collections.MinedObjectCollection;

import java.util.List;

public class MinedObjectMapper {

    public static MinedObject toMinedObject(MinedObjectCollection minedObjectCollection){

        return MinedObject.builder()
                .id(minedObjectCollection.id.toString())
                .userId(minedObjectCollection.getUserId())
                .pipelineId(minedObjectCollection.getPipelineId())
                .biologicalObjectId(minedObjectCollection.getBiologicalObjectId())
                .biologicalObjectParentId(minedObjectCollection.getBiologicalObjectParentId())
                .level(minedObjectCollection.getLevel())
                .build();
    }

    public static MinedObjectCollection toMinedObjectCollection(MinedObject minedObject){

        return MinedObjectCollection.builder()
                //TODO chequearID
                .userId(minedObject.getUserId())
                .pipelineId(minedObject.getPipelineId())
                .biologicalObjectId(minedObject.getBiologicalObjectId())
                .biologicalObjectParentId(minedObject.getBiologicalObjectParentId())
                .level(minedObject.getLevel())
                .build();
    }

    public static List<MinedObject> toMinedObjects(List<MinedObjectCollection> minedObjectCollections){

        return minedObjectCollections.stream().map(MinedObjectMapper::toMinedObject).toList();
    }
}
