package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.dtos.JasparRegionData;
import com.biopatternsg.infrastructure.dtos.JasparRegion;

public interface QueryJaspar {
    JasparRegionData getDataFromJasparSource(JasparRegion jasparRequest);
}
