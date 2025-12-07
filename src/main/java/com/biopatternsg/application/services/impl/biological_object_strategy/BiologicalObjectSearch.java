package com.biopatternsg.application.services.impl.biological_object_strategy;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectSearch implements ChainResponsibility {

    @NonNull
    public BiologicalObjectBySymbol biologicalObjectBySymbol;
    @NonNull
    public BiologicalObjectByHgncId biologicalObjectByHgncId;
    @NonNull
    public BiologicalObjectByUniprotId biologicalObjectByUniprotId;
    @NonNull
    public BiologicalObjectByUserUniprotId biologicalObjectByUserUniprotId;
    @NonNull
    public BiologicalObjectByUserHgncId biologicalObjectByUserHgncId;
    @NonNull
    public BiologicalObjectByUserSymbol biologicalObjectByUserSymbol;

    public ChainResponsibility next;

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

        setNext(biologicalObjectByUserUniprotId);
        biologicalObjectByUserUniprotId.setNext(biologicalObjectByUserHgncId);
        biologicalObjectByUserHgncId.setNext(biologicalObjectByUserSymbol);
        biologicalObjectByUserSymbol.setNext(biologicalObjectByUniprotId);
        biologicalObjectByUniprotId.setNext(biologicalObjectByHgncId);
        biologicalObjectByHgncId.setNext(biologicalObjectBySymbol);

        return next.request(biologicalObjectConfig);
    }
}
