package com.biopatternsg.infrastructure.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record JasparRequest(
        @NotNull(message = "Genome is required")
        String genome,

        @NotNull(message = "Track is required")
        String track,

        @NotNull(message = "Chromosome is required")
        String chromosome,

        @NotNull(message = "Start is required")
        String start,

        @NotNull(message = "End is required")
        String end,

        @NotNull(message = "Strand is required")
        String strand,

        @NotNull(message = "Reliability is required")
        @Positive(message = "Reliability must be positive")
        int reliability
) {
}
