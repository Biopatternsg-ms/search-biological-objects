package domain.port.out.external_repositories;

import domain.models.external_entities.HGNCResponse;


public interface HGNCRepository {

    HGNCResponse findHGNCInformation(String geneSymbol);

}
