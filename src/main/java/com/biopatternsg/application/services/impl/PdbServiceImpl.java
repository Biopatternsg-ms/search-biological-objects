package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BuildPdbService;
import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class PdbServiceImpl implements BuildPdbService {

    private final PdbRepository pdbeRepository;

    @Override
    public List<Complex> complexes(String uniprotId) {
        return pdbeRepository.getComplexes(uniprotId);
    }
}
