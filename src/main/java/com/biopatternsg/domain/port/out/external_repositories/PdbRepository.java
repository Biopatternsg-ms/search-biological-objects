package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.Complex;

import java.util.List;

public interface PdbRepository {

    List<Complex> getComplexes(String uniprotId);
}
