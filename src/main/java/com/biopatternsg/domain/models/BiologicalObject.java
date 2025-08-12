package com.biopatternsg.domain.models;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class BiologicalObject {

    private String id;
    private String symbol;
    private String name;
    private String locusType;
    private String ensemblGeneId;
    private Set<String> synonyms;
    private List<String> geneFamilies;
    private List<String> tissues;
    private Map<String, List<String>> uniprotId;
    private GeneOntology geneOntology;
}
