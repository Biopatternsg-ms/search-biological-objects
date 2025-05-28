package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.HGNCResponse;


public interface HGNCRepository {

    HGNCResponse findHGNCInformation(String geneSymbol);

}
