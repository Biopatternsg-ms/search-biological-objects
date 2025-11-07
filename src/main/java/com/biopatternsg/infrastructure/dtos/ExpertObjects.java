package com.biopatternsg.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExpertObjects(
        Long userId,
        Long experimentId,
        @JsonProperty("expertObjects")
        List<ExpertObjectsData> expertObjectsList
) {
}
