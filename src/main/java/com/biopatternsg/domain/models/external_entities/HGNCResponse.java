package com.biopatternsg.domain.models.external_entities;

import lombok.*;
import java.util.List;

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
    private List<String> synonym;
    private List<String> geneFamily;
}
