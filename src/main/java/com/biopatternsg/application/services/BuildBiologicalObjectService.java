package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.BiologicalObject;
import java.util.List;

public interface BuildBiologicalObjectService {

    List<BiologicalObject> execute(String geneSymbol);

    //BiologicalObject execute(TranscriptionFactor transcriptionFactor);

}
