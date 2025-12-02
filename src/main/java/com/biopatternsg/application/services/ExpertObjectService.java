package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.ExpertObjectConfig;

public interface ExpertObjectService {

    void execute(BiologicalObjectConfig expertObject, ExpertObjectConfig expertObjectConfig);
}
