package com.biopatternsg.infrastructure.adapters.dtos;

import com.biopatternsg.domain.models.BiologicalObject;

import java.util.List;

public record BiologicalObjectDTO(
        String id,
        String name,
        String symbol,
        List<String> synonyms
) {
    public static BiologicalObjectDTO fromDomain(BiologicalObject biologicalObject) {
        return new BiologicalObjectDTO(
                biologicalObject.getId(),
                biologicalObject.getName(),
                biologicalObject.getSymbol(),
                biologicalObject.getSynonyms().stream().toList()
        );
    }
}
