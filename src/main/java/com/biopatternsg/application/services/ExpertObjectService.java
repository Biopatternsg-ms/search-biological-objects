package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;

import java.util.List;

public interface ExpertObjectService {

    List<String> execute(List<BiologicalObjectConfig> expertObjects);

}
