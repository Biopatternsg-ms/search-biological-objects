package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;

public interface QueryBlat {
    String getByPromoterJasparRegion(PromoterRegionRequest promoterRegion);
}
