package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.BuildBlatSearchOptionsService;
import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.domain.port.out.external_repositories.BlatRepository;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@ApplicationScoped
public class BlatServiceImpl implements BuildBlatSearchOptionsService {

    private final BlatRepository blatRepository;

    @Override
    public List<BlatSearchOptionsResponse> execute(PromoterRegionRequest promoterRegion) {
        return blatRepository.fetchDataFromBlatSource(promoterRegion);
    }
}
