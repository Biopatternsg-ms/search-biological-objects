package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;

public interface BiologicalObjectRepository {

    BiologicalObject save(BiologicalObject biologicalObject);
    BiologicalObject findById(String id);
    BiologicalObject findByUniprotId(String uniprotId);
    BiologicalObject findByHgncId(String hgncId);
    BiologicalObject findBySymbol(String symbol);
    void update(BiologicalObject biologicalObject);
}
