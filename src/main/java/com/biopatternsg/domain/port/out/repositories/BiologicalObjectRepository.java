package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.infrastructure.dtos.ExpertObjectsData;
import com.biopatternsg.infrastructure.model_mongo.BiologicalObjectCollection;

import java.util.List;

public interface BiologicalObjectRepository {

    BiologicalObject save(BiologicalObject biologicalObject, Long userId);
    BiologicalObjectCollection searchDB(Long userId, ExpertObjectsData expertObjectsData);
}
