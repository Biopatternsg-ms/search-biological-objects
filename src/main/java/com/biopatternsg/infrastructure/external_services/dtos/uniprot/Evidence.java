package com.biopatternsg.infrastructure.external_services.dtos.uniprot;

public record Evidence(
        String evidenceCode,
        String source,
        String id
) {
}
