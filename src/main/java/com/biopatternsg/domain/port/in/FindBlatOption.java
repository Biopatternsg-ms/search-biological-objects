package com.biopatternsg.domain.port.in;

import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;

import java.util.List;

public interface FindBlatOption {
    List<BlatSearchOptionsResponse> execute(PromoterRegionRequest promoterRegionDTO);
}
