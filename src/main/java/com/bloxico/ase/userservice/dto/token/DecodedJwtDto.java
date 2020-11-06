package com.bloxico.ase.userservice.dto.token;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import static java.time.ZoneId.systemDefault;

@Value
public class DecodedJwtDto {

    Long userId;
    Set<String> roles;
    Set<String> permissions;

    public DecodedJwtDto(
                         Long userId,
                         Collection<String> roles,
                         Collection<String> permissions)
    {
        //HINT removed Issued at and expires at since spring security is handling that part
        this.userId = userId;
        // immutable set
        this.roles = Set.copyOf(roles);
        this.permissions = Set.copyOf(permissions);
    }

}
