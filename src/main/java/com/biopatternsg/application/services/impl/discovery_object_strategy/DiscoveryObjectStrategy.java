package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.domain.models.BiologicalObject;

import java.util.List;
import java.util.Set;

public interface DiscoveryObjectStrategy {

    void execute(Set<BiologicalObject> expertObjects, String pipelineId, int searchLevel);
    List<String> execute(String value);
}
