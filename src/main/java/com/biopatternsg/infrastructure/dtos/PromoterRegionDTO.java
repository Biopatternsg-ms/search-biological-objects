package com.biopatternsg.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PromoterRegionDTO(
        @NotNull(message = "Reliability is required")
        @Positive(message = "Reliability must be positive")
        int reliability,

        @NotBlank(message = "Promoter region is required")
        String promoterRegion
) {
}
