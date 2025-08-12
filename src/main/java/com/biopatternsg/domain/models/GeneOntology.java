package com.biopatternsg.domain.models;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GeneOntology {
    private List<String> cellularComponent;
    private List<String> molecularFunction;
    private List<String> biologicalProcess;

}
