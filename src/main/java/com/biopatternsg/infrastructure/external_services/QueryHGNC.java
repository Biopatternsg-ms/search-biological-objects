package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch.FetchResponse;
import com.biopatternsg.infrastructure.external_services.dtos.hgnc.search.SearchResponse;

public interface QueryHGNC {

    SearchResponse search(String label);

    FetchResponse fetch(String symbol);

}
