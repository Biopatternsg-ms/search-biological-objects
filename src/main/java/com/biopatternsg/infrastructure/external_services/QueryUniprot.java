package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.ListResponse;

public interface QueryUniprot {

    ListResponse search(String label);
    Response get(String label);
}
