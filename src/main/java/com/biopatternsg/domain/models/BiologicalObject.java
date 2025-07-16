package com.biopatternsg.domain.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> synonym;
    private List<String> geneFamily;
    private List<String> tissue;

    public BiologicalObject(){
        synonym = new ArrayList<>();
        geneFamily = new ArrayList<>();
        tissue = new ArrayList<>();
    }
}
