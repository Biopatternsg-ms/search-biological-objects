package com.biopatternsg.infrastructure.external_services.dtos.pdbe;

import java.util.List;

public record Group(
        List<Participant> participants,
        List<String> subcomplexes
) {
}
