package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.external_services.dtos.hgnc.HGNCGeneInformation;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.HGNCSymbol;

public interface QueryHGNC {

    HGNCSymbol search(String label);

    HGNCGeneInformation fetch(String symbol);

}
