package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.UniprotResponse;

public interface UniprotRepository {

    UniprotResponse findInfo(String uniprotIds);
}
