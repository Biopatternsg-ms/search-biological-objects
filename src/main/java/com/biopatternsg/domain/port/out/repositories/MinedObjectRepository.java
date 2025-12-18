package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.MinedObject;

public interface MinedObjectRepository {

    MinedObject save(MinedObject minedObject);
    MinedObject find(String biologicalObjectId, String pipelineId);
}
