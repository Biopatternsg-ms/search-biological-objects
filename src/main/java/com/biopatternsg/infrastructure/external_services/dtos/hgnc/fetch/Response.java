package com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch;

import java.util.List;

//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Builder

public record Response(
         int numFound,
         int start,
         boolean numFoundExact,
         List<Docs> docs
) {
}
