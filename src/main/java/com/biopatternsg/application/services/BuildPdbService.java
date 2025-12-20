package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.Complex;

import java.util.List;

public interface BuildPdbService {

    List<Complex> complexes(String uniprotId);
}
