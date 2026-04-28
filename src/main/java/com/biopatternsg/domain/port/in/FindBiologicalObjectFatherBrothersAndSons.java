package com.biopatternsg.domain.port.in;

import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDTO;

import java.util.List;

public interface FindBiologicalObjectFatherBrothersAndSons {
    List<BiologicalObjectDTO> execute(String pipelineId, String biologicalObjectId);
}
