package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.infrastructure.dtos.JasparRegion;
import com.biopatternsg.infrastructure.dtos.JasparRegionData;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.out.external_repositories.JasparRepository;
import com.biopatternsg.infrastructure.dtos.JasparRequest;
import com.biopatternsg.infrastructure.external_services.QueryJaspar;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@ApplicationScoped
public class JasparAdapter implements JasparRepository {

    private final QueryJaspar queryJaspar;

    public JasparAdapter(QueryJaspar queryJaspar) {
        this.queryJaspar = queryJaspar;
    }

    @Override
    public List<TranscriptionFactor> fetchDataFromJasparSource(JasparRequest jasparRequest) {

        JasparRegionData jasparRegionData = queryJaspar.getDataFromJasparSource(JasparRegion.of(jasparRequest));

        List<JasparRegionData.JasparTranscriptionFactor> jasparTranscriptionFactors = jasparRegionData.getTranscriptionFactors();
        int maxScore = Collections.max(jasparTranscriptionFactors, Comparator.comparingInt(JasparRegionData.JasparTranscriptionFactor::score)).score();

        float reliabilityScore = maxScore * ((float) jasparRequest.reliability() / 100);

        return jasparTranscriptionFactors.stream()
                .filter(jtf -> jtf.score() >= reliabilityScore)
                .map(jtf -> TranscriptionFactor.of(jtf, maxScore))
                .toList();
    }
}
