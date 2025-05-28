package com.biopatternsg.infrastructure.adapters.out;

import com.biopatternsg.infrastructure.adapters.in.JasparRegionDataFetcher;
import com.biopatternsg.infrastructure.dtos.JasparRegionDataRequest;
import com.biopatternsg.infrastructure.dtos.JasparTFRequest;
import com.biopatternsg.infrastructure.dtos.JasparRegionData;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.TranscriptionFactorJasparProviderPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.*;

@Slf4j
@ApplicationScoped
public class TranscriptionFactorJasparDataProviderAdapter implements TranscriptionFactorJasparProviderPort {

    private final JasparRegionDataFetcher jasparRegionDataFetcher;

    public TranscriptionFactorJasparDataProviderAdapter(@RestClient JasparRegionDataFetcher getJasparDataAdapter) {
        this.jasparRegionDataFetcher = getJasparDataAdapter;
    }

    @Override
    public List<TranscriptionFactor> fetchDataFromJasparSource(JasparTFRequest jasparRequest) {

        JasparRegionData jasparRegionData = jasparRegionDataFetcher.getRegionData(JasparRegionDataRequest.of(jasparRequest));

        List<JasparRegionData.JasparTranscriptionFactor> jasparTranscriptionFactors = jasparRegionData.getTranscriptionFactors();
        int maxScore = Collections.max(jasparTranscriptionFactors, Comparator.comparingInt(JasparRegionData.JasparTranscriptionFactor::score)).score();

        float reliabilityScore = maxScore * ((float) jasparRequest.reliability() / 100);

        return jasparTranscriptionFactors.stream()
                .filter(jtf -> jtf.score() >= reliabilityScore)
                .map(jtf -> TranscriptionFactor.of(jtf, maxScore))
                .toList();
    }
}
