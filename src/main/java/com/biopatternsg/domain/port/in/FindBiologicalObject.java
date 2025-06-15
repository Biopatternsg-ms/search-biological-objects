package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.TranscriptionFactor;

public interface FindBiologicalObject {
    BiologicalObject execute(String geneSymbol);
    BiologicalObject execute(TranscriptionFactor transcriptionFactor);
}
