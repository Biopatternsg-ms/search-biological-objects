package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;

public interface BiologicalObjectSearchService {

    BiologicalObject findByTypeAndValue(String type, String value);

    BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig);
}
