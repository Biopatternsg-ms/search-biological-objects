package com.biopatternsg.application.services.impl.biological_object_strategy;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;

public interface ChainResponsibility {

    void setNext(ChainResponsibility chainResponsibility);
    ChainResponsibility getNext();
    BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig);
}
