package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.BiologicalObject;

public interface BuildBiologicalObjectStrategy {

    BiologicalObject execute(String value);

}
