package domain.port;

import domain.models.BlatSearchOptions;
import infrastructure.dtos.PromoterRegionDTO;

import java.util.List;

public interface BlatProviderPort {
    List<BlatSearchOptions> fetchDataFromBlatSource(PromoterRegionDTO promoterRegionDTO);
}
