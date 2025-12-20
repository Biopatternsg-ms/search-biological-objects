package com.biopatternsg.application.services.impl.discovery_object_strategy;

import java.util.Objects;

public enum DiscoveryObjectEnum {

    PDB("pdb");

    private final String value;

    DiscoveryObjectEnum(String value) {
        this.value = value;
    }

    public static DiscoveryObjectEnum getValue(String value){

        for (DiscoveryObjectEnum headerEnum : DiscoveryObjectEnum.values()){
            if (Objects.equals(headerEnum.value, value)) {
                return headerEnum;
            }
        }
        return null;
    }
}
