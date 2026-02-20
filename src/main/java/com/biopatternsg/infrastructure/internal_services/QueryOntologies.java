package com.biopatternsg.infrastructure.internal_services;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;
import jakarta.ws.rs.core.Response;

public interface QueryOntologies {
    Response buildGeneOntologyTree(GeneOntology geneOntology);
    Response buildMeshOntologyTree(BiologicalObject biologicalObject);
}
