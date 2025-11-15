package com.biopatternsg.application.services.impl.biological_object_strategy;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectByUserHgncId implements BuildBiologicalObjectStrategy, ChainResponsibility {

    private final BiologicalObjectRepository biologicalObjectRepository;
    private ChainResponsibility next;

    @Override
    public BiologicalObject execute(String value) {
        return biologicalObjectRepository.findByHgncId(value);
    }

    @Override
    public void setNext(ChainResponsibility chainResponsibility) {

    }

    @Override
    public ChainResponsibility getNext() {
        return null;
    }

    @Override
    public BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig) {

        if(biologicalObjectConfig.getHgncId() == null || biologicalObjectConfig.getHgncId().isEmpty()){
            this.next.request(biologicalObjectConfig);
        }

        var biologicalObject = execute(biologicalObjectConfig.getHgncId());
        if(biologicalObject == null){
            this.next.request(biologicalObjectConfig);
        }

        return biologicalObject;
    }
}
