package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.BuildBlatSearchOptionsService;
import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.domain.port.in.FindBlatOption;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class GetBlatSearchOptionsUseCase implements FindBlatOption {

    private final BuildBlatSearchOptionsService blatSearchOptionsBuilder;

    @Override
    public List<BlatSearchOptionsResponse> execute(PromoterRegionRequest promoterRegion) {
        return blatSearchOptionsBuilder.execute(promoterRegion);
    }
}
