package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.MinedObject;

import java.util.List;

public interface MinedObjectRepository {

    MinedObject save(MinedObject minedObject);
    MinedObject find(String biologicalObjectId, String pipelineId);
    List<MinedObject> save(List<MinedObject> minedObjects);
    List<MinedObject> find(List<String> ids, String pipelineId);
    List<MinedObject> findByLevel(int level, String pipelineId);
    List<MinedObject> findByParentId(String biologicalObjectParentId, String pipelineId);
}
