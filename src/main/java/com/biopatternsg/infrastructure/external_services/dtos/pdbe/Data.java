package com.biopatternsg.infrastructure.external_services.dtos.pdbe;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Map;

public record Data(
        @JsonAnySetter
        Map<String, Group> group
) {
}
