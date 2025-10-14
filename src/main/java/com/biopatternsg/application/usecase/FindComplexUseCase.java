package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.BuildPdbService;
import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.port.in.FindComplex;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class FindComplexUseCase implements FindComplex {

    private final BuildPdbService buildPdbeService;

    @Override
    public List<Complex> execute(String uniprotId) {
        return buildPdbeService.complexes(uniprotId);
    }
}
