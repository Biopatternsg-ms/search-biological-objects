package com.biopatternsg.infrastructure.external_services.dtos.hgnc.search;

import java.util.List;

public record Response(
        int numFound,
        int start,
        double maxScore,
        boolean numFoundExact,
        List<Docs> docs
) {
}
