package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.infrastructure.dtos.JasparRequest;

import java.util.List;

public interface JasparRepository {
    List<TranscriptionFactor> fetchDataFromJasparSource(JasparRequest jasparRequest);
}
