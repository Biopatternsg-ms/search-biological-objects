package com.biopatternsg.domain.models;

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
public class BiologicalObject {

    private String id;
    private Long userId;
    private String symbol;
    private String name;
    private String locusType;
    private String ensemblGeneId;
    private String hgncId;
    private String uniprotId;
    private Set<String> synonyms;
    private GeneOntology geneOntology;
    private List<String> geneFamilies;
    private List<String> tissues;
    private TranscriptionFactor transcriptionFactor;
}
