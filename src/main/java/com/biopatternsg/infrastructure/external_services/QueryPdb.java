package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Response;

public interface QueryPdb {
    Response search(String uniprotId);
}
