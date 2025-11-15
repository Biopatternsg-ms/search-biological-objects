package com.biopatternsg.application.services.impl.biological_object_strategy;


import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectSearch implements ChainResponsibility {

    private final BiologicalObjectBySymbol biologicalObjectBySymbol;
    private final BiologicalObjectByHgncId biologicalObjectByHgncId;
    private final BiologicalObjectByUniprotId biologicalObjectByUniprotId;
    private final BiologicalObjectByUserUniprotId biologicalObjectByUserUniprotId;
    private final BiologicalObjectByUserHgncId biologicalObjectByUserHgncId;
    private final BiologicalObjectByUserSymbol biologicalObjectByUserSymbol;

    private ChainResponsibility next;

    @Override
    public void setNext(ChainResponsibility chainResponsibility) {
        this.next = chainResponsibility;
    }

    @Override
    public ChainResponsibility getNext() {
        return this.next;
    }

    @Override
    public BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig) {

        this.setNext(biologicalObjectByUserUniprotId);

        biologicalObjectByUserHgncId.setNext(biologicalObjectByUserSymbol);

        biologicalObjectByUserSymbol.setNext(biologicalObjectByUniprotId);

        biologicalObjectByUniprotId.setNext(biologicalObjectByHgncId);

        biologicalObjectByHgncId.setNext(biologicalObjectBySymbol);

        return next.request(biologicalObjectConfig);
    }
}
