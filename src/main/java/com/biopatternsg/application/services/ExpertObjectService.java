package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import java.util.List;

public interface ExpertObjectService {

    void execute(List<BiologicalObjectConfig> expertObjects, String pipelineId);
}
