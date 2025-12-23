package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.external_services.dtos.uniprot.ListResponse;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;

public interface QueryUniprot {

    ListResponse search(String label);
    Response get(String label);
}
