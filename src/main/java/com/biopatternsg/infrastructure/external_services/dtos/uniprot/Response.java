package com.biopatternsg.infrastructure.external_services.dtos.uniprot;

import java.util.List;

public record Response(
        List<EntryValue> results
) {
}
