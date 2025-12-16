package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.models.pipeline_config.TranscriptionFactorConfig;

import java.util.List;

public interface TranscriptionFactorsService {

    List<TranscriptionFactor> execute(TranscriptionFactorConfig transcriptionFactorConfig);
}
