package domain.port.in;

import domain.models.BiologicalObject;

public interface FindBiologicalObject {
    BiologicalObject execute(String geneSymbol);
}
