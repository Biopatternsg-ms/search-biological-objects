package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.TranscriptionFactorsService;
import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.models.pipeline_config.TranscriptionFactorConfig;
import com.biopatternsg.domain.port.out.external_repositories.JasparRepository;
import com.biopatternsg.domain.port.out.external_repositories.TFBindRepository;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class TranscriptionFactorsServiceImpl implements TranscriptionFactorsService {

    private final JasparRepository jasparRepository;
    private final TFBindRepository tfBindRepository;

    @Override
    public List<TranscriptionFactor> execute(TranscriptionFactorConfig transcriptionFactorConfig) {
        Map<String, TranscriptionFactor> transcriptionFactorMap = new HashMap<>();

        if(transcriptionFactorConfig.getSources().contains(TranscriptionFactorSource.JASPAR)){
            var jasparTranscriptionFactors = executeJaspar(transcriptionFactorConfig);
            transcriptionFactorMap.putAll(jasparTranscriptionFactors.stream().collect(Collectors.toMap(TranscriptionFactor::name, Function.identity())));
        }

        if(transcriptionFactorConfig.getSources().contains(TranscriptionFactorSource.TFBIND)){
            var tfBindTranscriptionsFactors = executeTFBind(transcriptionFactorConfig);

            tfBindTranscriptionsFactors.forEach(tfBindTranscriptionFactor -> {
                transcriptionFactorMap.putIfAbsent(tfBindTranscriptionFactor.name(), tfBindTranscriptionFactor);
            });
        }


        return transcriptionFactorMap.values().stream().toList();
    }

    private List<TranscriptionFactor> executeJaspar(TranscriptionFactorConfig transcriptionFactorConfig){
        JasparRequest jasparRequest = new JasparRequest(
                transcriptionFactorConfig.getGenome().getValue(),
                transcriptionFactorConfig.getTrack(),
                transcriptionFactorConfig.getChromosome(),
                transcriptionFactorConfig.getStart().toString(),
                transcriptionFactorConfig.getEnd().toString(),
                transcriptionFactorConfig.getStrand().getValue(),
                transcriptionFactorConfig.getReliability()
        );

        return jasparRepository.fetchDataFromJasparSource(jasparRequest);
    }

    public List<TranscriptionFactor> executeTFBind(TranscriptionFactorConfig transcriptionFactorConfig) {
        PromoterRegionRequest promoterRegionRequest = new PromoterRegionRequest(
                transcriptionFactorConfig.getReliability(),
                transcriptionFactorConfig.getPromoterRegion()
        );

        return tfBindRepository.fetchDataFromTFBindSource(promoterRegionRequest);
    }

}
