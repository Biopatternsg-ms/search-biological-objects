package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.Complex;

import java.util.List;

public interface FindComplex {

    List<Complex> execute(String uniprotId);

}
