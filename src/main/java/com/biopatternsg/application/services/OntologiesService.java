package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.GeneOntology;

public interface OntologiesService {
    void buildGeneOntologyTree(GeneOntology geneOntology);
}
