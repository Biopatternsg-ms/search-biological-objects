package com.biopatternsg.domain.models.pipeline_config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class MinedObjectConfig {

    private int level;
    private String pipelineId;
    private String parentId;
}
