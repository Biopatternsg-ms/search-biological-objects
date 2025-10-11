package com.biopatternsg.infrastructure.external_services.dtos.uniprot;

import java.util.List;

public record ProteinDescription(
        RecommendedName recommendedName,
        List<AlternativeName> alternativeNames

) {
}
