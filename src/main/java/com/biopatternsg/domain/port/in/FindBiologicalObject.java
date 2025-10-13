package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.BiologicalObject;

public interface FindBiologicalObject {

    BiologicalObject execute(String type, String value);
    //BiologicalObject execute(TranscriptionFactor transcriptionFactor);
}
