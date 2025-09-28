package com.biopatternsg.application.services.impl.BiologicalObjectStrategy;

import com.biopatternsg.application.services.BuildBiologicalObjectStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class BiologicalObjectContext {

    private final BuildBiologicalObjectBySymbol biologicalObjectBySymbol;
    private final BuildBiologicalObjectByHgncId biologicalObjectByHgncId;
    private final BuildBiologicalObjectByUniprotId biologicalObjectByUniprotId;

    public BuildBiologicalObjectStrategy load(String type){

        BiologicalObjectEnum value = BiologicalObjectEnum.getValue(type);
        assert value != null;
        return switch (value){
            case SYMBOL -> biologicalObjectBySymbol;
            case HGNC_ID -> biologicalObjectByHgncId;
            case UNIPROT_ID -> biologicalObjectByUniprotId;
        };
    }
}
