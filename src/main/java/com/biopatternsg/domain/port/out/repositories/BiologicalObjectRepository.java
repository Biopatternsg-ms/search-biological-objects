package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;

import java.util.List;

public interface BiologicalObjectRepository {

    BiologicalObject save(BiologicalObject biologicalObject);
    BiologicalObject findById(String id);
    List<BiologicalObject> findByIds(List<String> ids);
    BiologicalObject findByUniprotId(String uniprotId);
    BiologicalObject findByHgncId(String hgncId);
    BiologicalObject findBySymbol(String symbol);
}
