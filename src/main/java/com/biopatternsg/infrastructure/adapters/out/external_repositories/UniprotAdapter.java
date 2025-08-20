package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.UniprotResponse;
import com.biopatternsg.domain.port.out.external_repositories.UniprotRepository;
import com.biopatternsg.infrastructure.external_services.QueryUniprot;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.KBCrossReference;
import com.biopatternsg.infrastructure.external_services.dtos.uniprot.ProteinDescription;
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

        var values = queryUniprot.search(uniprotIds);
        var references = values.results().get(FIRST_VALUE).uniProtKBCrossReferences();
        var proteins = values.results().get(FIRST_VALUE).proteinDescription();
        var uniprotData = getCodesGO(references);
        var synonyms = getSynonyms(proteins);
        uniprotData.setSynonyms(synonyms);

        return uniprotData;
    }

    private static Set<String> getSynonyms(ProteinDescription descriptions){

        Set<String> synonyms = new HashSet<>();

        descriptions.alternativeNames().forEach(names -> synonyms.add(names.fullName().value()));
        synonyms.add(descriptions.recommendedName().fullName().value());

        return synonyms;
    }

    private static UniprotResponse getCodesGO(List<KBCrossReference> references){

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

        return UniprotResponse.builder()
                .goCc(goCc)
                .goMf(goMf)
                .goBp(goBp)
                .build();
    }
}
