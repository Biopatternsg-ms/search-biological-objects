package com.biopatternsg.infrastructure.dtos;

import com.biopatternsg.domain.enums.PipelineSteps;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PipelineStepInternalRequest(
        @NotNull String id,
        @NotNull PipelineSteps step
){
}
