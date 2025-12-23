package com.biopatternsg.infrastructure.external_services.dtos.pdbe;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.List;
import java.util.Map;

public record Response(
        @JsonAnySetter
        Map<String, List<Data>> uniprotIndex
) {
}
