package com.biopatternsg.application.services.impl.discovery_object_strategy;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class DiscoveryObjectContext {

    public final DiscoveryObjectByPdb discoveryObjectByPdb;

    public DiscoveryObjectStrategy load(String type){

        var value = DiscoveryObjectEnum.getValue(type);
        assert value != null;
        return switch (value){
            case PDB -> discoveryObjectByPdb;
        };
    }
}
