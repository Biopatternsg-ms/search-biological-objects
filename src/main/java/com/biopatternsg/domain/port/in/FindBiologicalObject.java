package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.BiologicalObject;

import java.util.List;

public interface FindBiologicalObject {

    List<BiologicalObject> execute(String geneSymbol);

    //BiologicalObject execute(TranscriptionFactor transcriptionFactor);
}
