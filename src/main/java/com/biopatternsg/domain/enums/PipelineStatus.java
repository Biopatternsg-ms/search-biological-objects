package com.biopatternsg.domain.enums;

public enum PipelineStatus {

    CONFIG("configuration"),
    FACTOR("factor_transcription"),
    OBJECTS("expert_object"),
    LEVELS("search_level");

    private final String value;

    PipelineStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
