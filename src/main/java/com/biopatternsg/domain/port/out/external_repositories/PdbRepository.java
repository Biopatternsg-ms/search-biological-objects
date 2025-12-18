package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.Complex;

import java.util.List;
import java.util.Map;

public interface PdbRepository {

    Map<String, List<String>> getParticipants(String uniprotId);
    List<Complex> getComplexes(String uniprotId);
}
