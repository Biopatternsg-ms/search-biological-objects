package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;

public interface OntologiesService {
    void buildGeneOntologyTree(GeneOntology geneOntology);
    void buildMeshOntologyTree(BiologicalObject biologicalObject);
}
