package com.biopatternsg.domain.models.external_entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private String uniprotId;
    private Set<String> synonyms;
    private List<String> geneFamilies;
}
