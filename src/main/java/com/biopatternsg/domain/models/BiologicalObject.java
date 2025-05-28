package com.biopatternsg.domain.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BiologicalObject {
    private String id;
    private String name;
    private TranscriptionFactor transcriptionFactor;
}
