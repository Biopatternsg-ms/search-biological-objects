/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.BuildTranscriptionFactorsService;
import com.biopatternsg.application.services.TranscriptionFactorsService;
import com.biopatternsg.domain.models.JasparQuery;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.models.pipeline_config.TranscriptionFactorConfig;
import com.biopatternsg.domain.port.in.FindTranscriptionFactor;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class GetTranscriptionFactorsUseCase implements FindTranscriptionFactor {

    private final BuildTranscriptionFactorsService buildTranscriptionFactorsService;
    private final TranscriptionFactorsService transcriptionFactorsService;

    @Override
    public List<TranscriptionFactor> getJasparTranscriptionFactors(JasparQuery jasparQuery) {
        return buildTranscriptionFactorsService.executeJaspar(jasparQuery);
    }

    @Override
    public List<TranscriptionFactor> getTFBindTranscriptionFactors(int reliability, String promoterRegion) {
        return buildTranscriptionFactorsService.executeTFBind(reliability, promoterRegion);
    }

    @Override
    public List<TranscriptionFactor> getTranscriptionFactorsByConfig(TranscriptionFactorConfig transcriptionFactorConfig) {
        return transcriptionFactorsService.executeGetTranscriptionFactors(transcriptionFactorConfig);
    }
}
