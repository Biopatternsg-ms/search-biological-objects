package com.biopatternsg.domain.models.pipeline_config;

import com.biopatternsg.domain.enums.PipelineSteps;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PipelineConfig {

    @NonNull
    private String pipelineId;
    @NonNull
    private Integer levels;
    @NonNull
    private List<BiologicalObjectConfig> expertObjects;
    private TranscriptionFactorConfig transcriptionFactorConfig;
}
