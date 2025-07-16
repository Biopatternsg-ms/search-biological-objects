package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import java.util.List;

public interface BiologicalObjectRepository {

    List<BiologicalObject> save(List<BiologicalObject> biologicalObject);

}
