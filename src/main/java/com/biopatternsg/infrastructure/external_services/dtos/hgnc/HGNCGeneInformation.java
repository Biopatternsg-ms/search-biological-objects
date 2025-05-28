package com.biopatternsg.infrastructure.external_services.dtos.hgnc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HGNCGeneInformation {
    private String name;
}
