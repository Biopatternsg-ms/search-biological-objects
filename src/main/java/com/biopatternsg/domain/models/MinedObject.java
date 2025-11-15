package com.biopatternsg.domain.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MinedObject {

    private String id;
    private String userId;
    private String pipelineId;
    private String biologicalObjectId;
    private int level;
}
