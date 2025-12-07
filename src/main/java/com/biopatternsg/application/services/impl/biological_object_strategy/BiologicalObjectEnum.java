package com.biopatternsg.application.services.impl.biological_object_strategy;

import java.util.Objects;

public enum BiologicalObjectEnum {

    SYMBOL("symbol"), HGNC_ID("hgnc"),
    UNIPROT_ID("uniprot"), USER_SYMBOL("user_symbol"),
    USER_HGNC_ID("user_hgnc"), USER_UNIPROT_ID("user_uniprot");

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
