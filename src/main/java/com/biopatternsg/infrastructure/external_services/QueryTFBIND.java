package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;

public interface QueryTFBIND {
    String getByPromoterRegion(PromoterRegionRequest promoterRegion);
}
