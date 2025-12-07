package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.pipeline_config.TranscriptionFactorConfig;

public interface TranscriptionFactorsService {

    void execute(TranscriptionFactorConfig transcriptionFactorConfig);
}
