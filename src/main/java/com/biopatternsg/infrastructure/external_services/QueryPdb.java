package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.external_services.dtos.pdbe.Response;

public interface QueryPdb {
    Response search(String uniprotId);
}
