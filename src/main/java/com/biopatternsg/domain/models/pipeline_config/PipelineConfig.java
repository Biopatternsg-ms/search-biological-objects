package com.biopatternsg.domain.models.pipeline_config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PipelineConfig {

    private String pipelineId;
    private List<BiologicalObjectConfig> expertObjects;
    private TranscriptionFactorConfig transcriptionFactorConfig;
}
