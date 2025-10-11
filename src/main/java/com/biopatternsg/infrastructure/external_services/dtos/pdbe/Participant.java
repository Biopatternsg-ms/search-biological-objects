package com.biopatternsg.infrastructure.external_services.dtos.pdbe;

public record Participant(
        String accession,
        int stoichiometry,
        int taxonomy_id
) {}