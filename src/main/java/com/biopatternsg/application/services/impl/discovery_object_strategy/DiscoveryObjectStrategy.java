package com.biopatternsg.application.services.impl.discovery_object_strategy;

import com.biopatternsg.domain.models.BiologicalObject;

import java.util.Set;

public interface DiscoveryObjectStrategy {

    void execute(Set<BiologicalObject> expertObjects, String pipelineId, int searchLevel);
}
