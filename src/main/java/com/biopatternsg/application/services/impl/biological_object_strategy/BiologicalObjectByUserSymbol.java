package com.biopatternsg.application.services.impl.biological_object_strategy;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectByUserSymbol implements BuildBiologicalObjectStrategy, ChainResponsibility {

    private final BiologicalObjectRepository biologicalObjectRepository;
    private ChainResponsibility next;

    @Override
    public BiologicalObject execute(String value) {
        return biologicalObjectRepository.findBySymbol(value);
    }

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

        if(biologicalObjectConfig.getSymbol() == null || biologicalObjectConfig.getSymbol().isEmpty()){
            this.next.request(biologicalObjectConfig);
        }

        var biologicalObject = execute(biologicalObjectConfig.getSymbol());
        if(biologicalObject == null){
            this.next.request(biologicalObjectConfig);
        }

        return biologicalObject;
    }
}
