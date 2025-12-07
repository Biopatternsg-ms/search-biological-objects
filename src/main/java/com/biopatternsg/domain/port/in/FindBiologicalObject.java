package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.infrastructure.dtos.ExpertObjects;

import java.util.List;

public interface FindBiologicalObject {

    BiologicalObject execute(String type, String value);
    //BiologicalObject execute(TranscriptionFactor transcriptionFactor);
}
