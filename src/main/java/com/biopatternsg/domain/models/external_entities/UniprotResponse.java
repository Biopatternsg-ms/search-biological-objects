package com.biopatternsg.domain.models.external_entities;

import lombok.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UniprotResponse {

    private String geneSymbol;
    private List<String> goCc;
    private List<String> goMf;
    private List<String> goBp;
    private Set<String> synonyms;
}
