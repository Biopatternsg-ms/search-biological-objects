package application.services;

import domain.models.BiologicalObject;
import domain.models.TranscriptionFactor;

public interface BuildBiologicalObjectService {

    BiologicalObject execute(String geneSymbol);

    BiologicalObject execute(TranscriptionFactor transcriptionFactor);

}
