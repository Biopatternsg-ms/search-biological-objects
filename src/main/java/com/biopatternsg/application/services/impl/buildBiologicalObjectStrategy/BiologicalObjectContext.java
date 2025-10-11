package com.biopatternsg.application.services.impl.buildBiologicalObjectStrategy;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class BiologicalObjectContext {

    private final BiologicalObjectBySymbol biologicalObjectBySymbol;
    private final BiologicalObjectByHgncId biologicalObjectByHgncId;
    private final BiologicalObjectByUniprotId biologicalObjectByUniprotId;

    public BiologicalObjectStrategy load(String type){

        BiologicalObjectEnum value = BiologicalObjectEnum.getValue(type);
        assert value != null;
        return switch (value){
            case SYMBOL -> biologicalObjectBySymbol;
            case HGNC_ID -> biologicalObjectByHgncId;
            case UNIPROT_ID -> biologicalObjectByUniprotId;
        };
    }

}
