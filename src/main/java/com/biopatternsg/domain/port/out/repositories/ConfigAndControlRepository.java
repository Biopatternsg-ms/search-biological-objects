package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.enums.PipelineSteps;

public interface ConfigAndControlRepository {

    void updatePipelineStep(String pipelineId, PipelineSteps pipelineStep);
}
