package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.UniprotResponse;
import com.biopatternsg.domain.port.out.external_repositories.UniprotRepository;
import com.biopatternsg.infrastructure.external_services.QueryUniprot;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.KBCrossReference;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.ProteinDescription;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.Response;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UniprotAdapter implements UniprotRepository {

    private final QueryUniprot queryUniprot;
    private static final int FIRST_VALUE = 0;

    @Override
    public UniprotResponse findInfo(String uniprotIds) {

        var response = queryUniprot.search(uniprotIds);
        if(response.results().isEmpty()){
            return null;
        }
        return formatUniprotInformation(response.results().getFirst());
    }

    private UniprotResponse formatUniprotInformation(Response response){

        var uniprotResponse = UniprotResponse.builder()
                .id(response.primaryAccession())
                .name(response.proteinDescription().recommendedName().fullName().value())
                .symbol(response.genes().getFirst().geneName().value())
                .build();

        var references = response.uniProtKBCrossReferences();
        var proteins = response.proteinDescription();
        setCodesGO(uniprotResponse, references);
        setSynonyms(uniprotResponse, proteins);

        return uniprotResponse;
    }

    private static void setCodesGO(UniprotResponse response, List<KBCrossReference> references){

        List<String> goCc = new ArrayList<>();
        List<String> goMf = new ArrayList<>();
        List<String> goBp = new ArrayList<>();

        references.forEach(reference -> reference.properties().forEach(property -> {
            String branch = property.value().split(":")[FIRST_VALUE];
            switch (branch) {
                case "C" -> goCc.add(reference.id());
                case "F" -> goMf.add(reference.id());
                case "P" -> goBp.add(reference.id());
            }
        }));

        response.setGoCc(goCc);
        response.setGoMf(goMf);
        response.setGoBp(goBp);
    }

    private static void setSynonyms(UniprotResponse response, ProteinDescription descriptions){

        Set<String> synonyms = new HashSet<>();
        if(descriptions.alternativeNames() != null){
            descriptions.alternativeNames().forEach(names -> synonyms.add(names.fullName().value()));
            synonyms.add(descriptions.recommendedName().fullName().value());
        }
        response.setSynonyms(synonyms);
    }
}
