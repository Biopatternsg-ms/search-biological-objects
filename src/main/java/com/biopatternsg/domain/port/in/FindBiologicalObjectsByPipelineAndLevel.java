package com.biopatternsg.domain.port.in;

import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDTO;

import java.util.List;

public interface FindBiologicalObjectsByPipelineAndLevel {
    List<BiologicalObjectDTO> execute(String pipelineId, int level);
}
