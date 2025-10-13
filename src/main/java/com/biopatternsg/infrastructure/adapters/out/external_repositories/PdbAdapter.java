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

        Response response = queryPdbe.search(uniprotId);

        Map<String, List<String>> pivotMap = new HashMap<>();
        List<Data> dataList = response.uniprotIndex().get(uniprotId);
        if (dataList != null && !dataList.isEmpty()) {
            for (Data groupsList : dataList) {
                Map<String, Group> participantsList = groupsList.group();
                for (Map.Entry<String, Group> entry : participantsList.entrySet()) {
                    String keyValue = entry.getKey();
                    Group groupValue = entry.getValue();
                    var participants = groupValue.participants().stream()
                            .map(Participant::accession)
                            .filter(accession -> !accession.equals(uniprotId))
                            .toList();

                    participants.forEach(item-> addComplexes(pivotMap,item,keyValue));
                }
            }
        }

        return pivotMap;
    }

    @Override
    public List<Complex> getComplexes(String uniprotId) {

        Response response = queryPdbe.search(uniprotId);
        List<Data> dataList = response.uniprotIndex().get(uniprotId);

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

    private static void addComplexes(Map<String, List<String>> map, String clave, String valor) {
        map.computeIfAbsent(clave, k -> new ArrayList<>()).add(valor);
    }
}
