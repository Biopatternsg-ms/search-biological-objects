package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;

import java.util.List;

public interface BlatRepository {
    List<BlatSearchOptionsResponse> fetchDataFromBlatSource(PromoterRegionRequest promoterRegionDTO);
}
