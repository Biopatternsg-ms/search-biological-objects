package com.biopatternsg.infrastructure.external_services.dtos.uniprot;

import java.util.List;

public record Gene(
        GeneName geneName,
        List<Synonym> synonyms
) {
}
