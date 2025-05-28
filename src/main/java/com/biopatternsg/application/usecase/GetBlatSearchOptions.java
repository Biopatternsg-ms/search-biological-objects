package com.biopatternsg.application.usecase;

import com.biopatternsg.domain.models.BlatSearchOptions;
import com.biopatternsg.domain.port.BlatProviderPort;
import com.biopatternsg.infrastructure.dtos.PromoterRegionDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetBlatSearchOptions {

    private final BlatProviderPort blatProviderPort;

    public GetBlatSearchOptions(BlatProviderPort transcriptionFactorJasparProviderPort) {
        this.blatProviderPort = transcriptionFactorJasparProviderPort;
    }

    public List<BlatSearchOptions> getBlatSearchOptions(PromoterRegionDTO tfRequest) {
        return blatProviderPort.fetchDataFromBlatSource(tfRequest);
    }
}
