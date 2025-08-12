package com.biopatternsg.domain.models.external_entities;

import lombok.*;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Builder

public class HGNCResponse {

    private String id;
    private String symbol;
    private String name;
    private String locusType;
    private String ensemblGeneId;
    private Set<String> synonyms;
    private List<String> geneFamilies;
    private List<String> uniprotIds;
}
