package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.GeneOntology;
import jakarta.ws.rs.core.Response;

public interface OntologiesRepository {
    Response buildGeneOntologyTree(GeneOntology geneOntology);
}
