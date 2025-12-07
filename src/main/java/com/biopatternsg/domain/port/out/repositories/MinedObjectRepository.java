package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.MinedObject;

import java.util.List;

public interface MinedObjectRepository {

    MinedObject save(MinedObject minedObject);
    MinedObject find(String biologicalObjectId, String pipelineId);
}
