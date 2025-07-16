package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.HGNCResponse;

import java.util.List;


public interface HGNCRepository {

    List<HGNCResponse> findHGNCInformation(String geneSymbol);
}
