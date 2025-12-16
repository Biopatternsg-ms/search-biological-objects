package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.domain.port.out.external_repositories.OntologiesRepository;
import com.biopatternsg.infrastructure.internal_services.QueryOntologies;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class OntologiesAdapter implements OntologiesRepository {

    private final QueryOntologies queryOntologies;

    @Override
    public Response buildGeneOntologyTree(GeneOntology geneOntology) {
        return queryOntologies.buildGeneOntologyTree(geneOntology);
    }
}
