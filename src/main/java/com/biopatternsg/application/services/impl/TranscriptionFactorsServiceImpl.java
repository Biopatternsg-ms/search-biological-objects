package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.OntologiesService;
import com.biopatternsg.application.services.TranscriptionFactorsService;
import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch;
import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import com.biopatternsg.domain.models.pipeline_config.TranscriptionFactorConfig;
import com.biopatternsg.domain.port.out.external_repositories.JasparRepository;
import com.biopatternsg.domain.port.out.external_repositories.TFBindRepository;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.UserRepository;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class TranscriptionFactorsServiceImpl implements TranscriptionFactorsService {

    private final JasparRepository jasparRepository;
    private final TFBindRepository tfBindRepository;
    private final BiologicalObjectSearch biologicalObjectSearch;
    private final BiologicalObjectRepository biologicalObjectRepository;
    private final OntologiesService ontologiesService;
    private final UserRepository userRepository;

    @Override
    public List<String> execute(TranscriptionFactorConfig transcriptionFactorConfig) {
        Map<String, TranscriptionFactor> transcriptionFactorMap = new HashMap<>();

        if(transcriptionFactorConfig.getSources().contains(TranscriptionFactorSource.JASPAR)){
            var jasparTranscriptionFactors = executeJaspar(transcriptionFactorConfig);
            transcriptionFactorMap.putAll(jasparTranscriptionFactors.stream().collect(Collectors.toMap(TranscriptionFactor::name, Function.identity())));
        }

        if(transcriptionFactorConfig.getSources().contains(TranscriptionFactorSource.TFBIND)){
            var tfBindTranscriptionsFactors = executeTFBind(transcriptionFactorConfig);

            tfBindTranscriptionsFactors.forEach(tfBindTranscriptionFactor ->
                transcriptionFactorMap.putIfAbsent(tfBindTranscriptionFactor.name(), tfBindTranscriptionFactor)
            );
        }

        return getBiologicalObjectIds(transcriptionFactorMap);
    }

    private List<String> getBiologicalObjectIds(Map<String, TranscriptionFactor> transcriptionFactorMap) {
        List<TranscriptionFactor> transcriptionFactors = transcriptionFactorMap.values().stream().toList();

        List<String> biologicalObjectIds = new ArrayList<>();
        var userId = userRepository.getUserId();

        transcriptionFactors.forEach(transcriptionFactor -> {

            var biologicalObjectConfig = BiologicalObjectConfig.builder()
                    .symbol(transcriptionFactor.name())
                    .build();

            var biologicalObject = biologicalObjectSearch.request(biologicalObjectConfig);

            if(biologicalObject.getId() == null){
                biologicalObject.setTranscriptionFactor(transcriptionFactor);
                biologicalObject.setUserId(userId);
                biologicalObject = biologicalObjectRepository.save(biologicalObject);
                ontologiesService.buildGeneOntologyTree(biologicalObject.getGeneOntology());
            }

            biologicalObjectIds.add(biologicalObject.getId());
        });

        return biologicalObjectIds;
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
