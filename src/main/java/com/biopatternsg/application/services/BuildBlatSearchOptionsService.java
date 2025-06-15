package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;

import java.util.List;

public interface BuildBlatSearchOptionsService {
    List<BlatSearchOptionsResponse> execute(PromoterRegionRequest promoterRegionDTO);
}
