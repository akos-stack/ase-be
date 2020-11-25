package com.bloxico.ase.userservice.dto.token;

import lombok.Value;

import java.util.Collection;
import java.util.Set;

@Value
public class DecodedJwtDto {

    Long userId;
    Set<String> roles;

    public DecodedJwtDto(Long userId, Collection<String> roles) {
        this.userId = userId;
        this.roles = Set.copyOf(roles); // immutable set
    }

}
