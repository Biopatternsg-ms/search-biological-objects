package com.biopatternsg.domain.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MinedObject {

    private String id;
    private Long userId;
    private String pipelineId;
    private String biologicalObjectId;
    private String uniprotId;
    private String uniprotIdFather;
    private int level;
}
