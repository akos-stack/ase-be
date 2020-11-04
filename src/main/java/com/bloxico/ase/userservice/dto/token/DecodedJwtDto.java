package com.bloxico.ase.userservice.dto.token;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import static java.time.ZoneId.systemDefault;

@Value
public class DecodedJwtDto {

    LocalDateTime issuedAt;
    LocalDateTime expiresAt;
    Long userId;
    Set<String> roles;
    Set<String> permissions;

    public DecodedJwtDto(Date issuedAt,
                         Date expiresAt,
                         Long userId,
                         Collection<String> roles,
                         Collection<String> permissions)
    {
        this.issuedAt = issuedAt.toInstant().atZone(systemDefault()).toLocalDateTime();
        this.expiresAt = expiresAt.toInstant().atZone(systemDefault()).toLocalDateTime();
        this.userId = userId;
        // immutable set
        this.roles = Set.copyOf(roles);
        this.permissions = Set.copyOf(permissions);
    }

}
