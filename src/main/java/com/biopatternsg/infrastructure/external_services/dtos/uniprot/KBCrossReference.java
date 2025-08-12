package com.biopatternsg.infrastructure.external_services.dtos.uniprot;

import java.util.List;

public record KBCrossReference(
        String database,
        String id,
        List<Property> properties,
        List<Evidence> evidences
) {
}
