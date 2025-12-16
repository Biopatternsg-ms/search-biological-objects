package com.biopatternsg.application.services.impl;

import com.biopatternsg.application.services.OntologiesService;
import com.biopatternsg.domain.models.GeneOntology;
import com.biopatternsg.domain.port.out.external_repositories.OntologiesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class OntologiesServiceImpl implements OntologiesService {

    private final OntologiesRepository ontologiesRepository;

    @Override
    public void buildGeneOntologyTree(GeneOntology geneOntology) {
        if (geneOntology != null) {
            Response response = ontologiesRepository.buildGeneOntologyTree(geneOntology);
            log.info(response.readEntity(String.class));
        }
    }
}
