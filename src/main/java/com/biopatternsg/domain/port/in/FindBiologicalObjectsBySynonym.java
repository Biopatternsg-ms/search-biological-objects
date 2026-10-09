package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.BiologicalObject;
import java.util.List;

public interface FindBiologicalObjectsBySynonym {
    List<BiologicalObject> execute(String synonym);
}
