package com.biopatternsg.infrastructure.external_services.dtos.uniprot;

import java.util.List;

public record EntryValue(
        String entryType,
        String primaryAccession,
        ProteinDescription proteinDescription,
        List<KBCrossReference> uniProtKBCrossReferences,
        ExtraAttributes extraAttributes
) {
}
