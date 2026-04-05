package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import com.biopatternsg.infrastructure.external_services.QueryPdb;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Data;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Participants;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe_complex.Response;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PdbAdapter implements PdbRepository {

    private final QueryPdb queryPdbe;

    @Override
    public List<Complex> getComplexes(String uniprotId) {

        List<Data> dataList = getPdbResponse(uniprotId);
        return dataList.stream()
                .map(data -> {
                    String complexId = data.pdbComplexId();
                    List<String> participants = data.participants().stream()
                            .map(Participants::accession)
                            .filter(accession -> !accession.equals(uniprotId))
                            .toList();

                    return new Complex(complexId, participants);
                })
                .toList();
    }

    private List<Data> getPdbResponse(String uniprotId) {

        try{
            Response response = queryPdbe.search(uniprotId);
            return Optional.ofNullable(response)
                    .map(Response::uniprotIndex)
                    .map(map -> map.get(uniprotId))
                    .orElse(Collections.emptyList());

        } catch (Exception e) {
            log.info("Error con PDBe: {}", e.getMessage());
            return List.of();
        }
    }
}
