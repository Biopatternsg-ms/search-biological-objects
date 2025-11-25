package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.infrastructure.dtos.ExpertObjectsData;
import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;

public interface BiologicalObjectRepository {

    BiologicalObject save(BiologicalObject biologicalObject);
    BiologicalObject findByUniprotId(String uniprotId);
    BiologicalObject findByHgncId(String hgncId);
    BiologicalObject findBySymbol(String symbol);
}
