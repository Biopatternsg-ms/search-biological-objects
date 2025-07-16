package com.biopatternsg.infrastructure.external_services.dtos.hgnc.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Docs(
        String symbol,
        double score,
        @JsonProperty("hgnc_id") String hgncId
) {
}
