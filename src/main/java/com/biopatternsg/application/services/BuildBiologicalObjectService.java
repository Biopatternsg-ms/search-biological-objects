package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.TranscriptionFactor;

public interface BuildBiologicalObjectService {

    BiologicalObject execute(String geneSymbol);

    BiologicalObject execute(TranscriptionFactor transcriptionFactor);

}
