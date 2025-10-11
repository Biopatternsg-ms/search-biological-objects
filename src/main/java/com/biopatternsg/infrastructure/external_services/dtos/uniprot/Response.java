package com.biopatternsg.infrastructure.external_services.dtos.uniprot;

import java.util.List;

public record Response(
        String entryType,
        String primaryAccession,
        List<String> secondaryAccessions,
        String uniProtkbId,
        ProteinDescription proteinDescription,
        List<Gene> genes,
        List<KBCrossReference> uniProtKBCrossReferences,
        ExtraAttributes extraAttributes
) {
}
