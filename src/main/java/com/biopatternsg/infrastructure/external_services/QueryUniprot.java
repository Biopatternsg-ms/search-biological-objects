package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;

public interface QueryUniprot {

    Response search(String label);
}
