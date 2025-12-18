package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.MinedObjectConfig;

public interface BiologicalObjectService {

    BiologicalObject execute(BiologicalObjectConfig expertObject, MinedObjectConfig minedObjectConfig);
}
