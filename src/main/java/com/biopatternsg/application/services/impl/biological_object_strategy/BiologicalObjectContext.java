package com.biopatternsg.application.services.impl.biological_object_strategy;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class BiologicalObjectContext{

    private final BiologicalObjectBySymbol biologicalObjectBySymbol;
    private final BiologicalObjectByHgncId biologicalObjectByHgncId;
    private final BiologicalObjectByUniprotId biologicalObjectByUniprotId;
    private final BiologicalObjectByUserUniprotId biologicalObjectByUserUniprotId;
    private final BiologicalObjectByUserHgncId biologicalObjectByUserHgncId;
    private final BiologicalObjectByUserSymbol biologicalObjectByUserSymbol;

    public BuildBiologicalObjectStrategy load(String type){

        BiologicalObjectEnum value = BiologicalObjectEnum.getValue(type);
        assert value != null;
        return switch (value){
            case SYMBOL -> biologicalObjectBySymbol;
            case HGNC_ID -> biologicalObjectByHgncId;
            case UNIPROT_ID -> biologicalObjectByUniprotId;
            case USER_SYMBOL -> biologicalObjectByUserSymbol;
            case USER_HGNC_ID -> biologicalObjectByUserHgncId;
            case USER_UNIPROT_ID -> biologicalObjectByUserUniprotId;
        };
    }
}
