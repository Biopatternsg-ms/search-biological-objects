package com.biopatternsg.infrastructure.external_services;

import com.biopatternsg.infrastructure.dtos.JasparRegion;
import com.biopatternsg.infrastructure.dtos.JasparRegionData;

public interface QueryJaspar {
    JasparRegionData getDataFromJasparSource(JasparRegion jasparRequest);
}
