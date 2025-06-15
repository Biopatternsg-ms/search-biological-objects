package com.biopatternsg.domain.models.external_entities;

import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
public record BlatSearchOptionsResponse(
        double identity,
        String chromosome,
        String strand,
        String start,
        String end
) {
    public static BlatSearchOptionsResponse of(String blatSearchOptionsString) {
        List<String> parts = Arrays.stream(blatSearchOptionsString.split(" ")).toList();

        return BlatSearchOptionsResponse.builder()
                .identity(Double.parseDouble(parts.get(5).replace("%", "")))
                .chromosome(parts.get(6))
                .strand(parts.get(7))
                .start(parts.get(8))
                .end(parts.get(9))
                .build();
    }
}
