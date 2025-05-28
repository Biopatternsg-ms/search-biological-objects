package infrastructure.external_services;

import infrastructure.external_services.dtos.hgnc.HGNCGeneInformation;
import infrastructure.external_services.dtos.hgnc.HGNCSymbol;

public interface QueryHGNC {

    HGNCSymbol search(String label);

    HGNCGeneInformation fetch(String symbol);

}
