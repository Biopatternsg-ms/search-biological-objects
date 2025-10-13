package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.port.out.external_repositories.PdbRepository;
import com.biopatternsg.infrastructure.external_services.QueryPdb;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe.Data;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe.Group;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe.Participant;
import com.biopatternsg.infrastructure.external_services.dtos.pdbe.Response;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PdbAdapter implements PdbRepository {

    private final QueryPdb queryPdbe;

    @Override
    public Map<String, List<String>> getParticipants(String uniprotId) {

        List<Data> dataList = getPdbResponse(uniprotId);
        return Optional.ofNullable(dataList)
                .orElseGet(Collections::emptyList)
                .stream()
                .flatMap(data -> data.group().entrySet().stream())
                .flatMap(complexEntry -> {
                    String complexId = complexEntry.getKey();
                    Group group = complexEntry.getValue();
                    return group.participants().stream()
                            .map(Participant::accession)
                            .filter(participantId -> !participantId.equals(uniprotId))
                            .map(participantId -> Map.entry(participantId, complexId));
                })
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    @Override
    public List<Complex> getComplexes(String uniprotId) {

        List<Data> dataList = getPdbResponse(uniprotId);
        return Stream.of(dataList)
                .flatMap(List::stream)
                .flatMap(data -> data.group().entrySet().stream())
                .map(entry -> {
                    String complexId = entry.getKey();
                    Group group = entry.getValue();
                    List<String> participants = group.participants().stream()
                            .map(Participant::accession)
                            .filter(accession -> !accession.equals(uniprotId))
                            .toList();

                    return new Complex(complexId, participants);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Data> getPdbResponse(String uniprotId) {
        Response response = queryPdbe.search(uniprotId);
        return Optional.ofNullable(response)
                .map(Response::uniprotIndex)
                .map(map -> map.get(uniprotId))
                .orElse(Collections.emptyList());
    }
}
