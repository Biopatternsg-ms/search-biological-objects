package com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Data(

        String name,
        @JsonProperty("pdb_complex_id")
        String pdbComplexId,
        List<Participants> participants,
        List<String> subcomplexes
) {
}
