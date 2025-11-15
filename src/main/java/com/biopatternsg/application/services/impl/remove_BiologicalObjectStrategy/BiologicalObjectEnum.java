package com.biopatternsg.application.services.impl.remove_BiologicalObjectStrategy;

import java.util.Objects;

public enum BiologicalObjectEnum {
    SYMBOL("symbol"), HGNC_ID("hgnc"), UNIPROT_ID("uniprot");

    private final String value;

    BiologicalObjectEnum(String value) {
        this.value = value;
    }

    public static BiologicalObjectEnum getValue(String value) {
        for (BiologicalObjectEnum headerEnum : BiologicalObjectEnum.values()) {
            if (Objects.equals(headerEnum.value, value)) {
                return headerEnum;
            }
        }
        return null;
    }
}
