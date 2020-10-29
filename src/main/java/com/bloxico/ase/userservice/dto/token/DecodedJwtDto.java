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
    String role;
    Set<String> permissions;

    public DecodedJwtDto(Date issuedAt,
                         Date expiresAt,
                         Long userId,
                         String role,
                         Collection<String> permissions)
    {
        this.issuedAt = issuedAt.toInstant().atZone(systemDefault()).toLocalDateTime();
        this.expiresAt = expiresAt.toInstant().atZone(systemDefault()).toLocalDateTime();
        this.userId = userId;
        this.role = role;
        this.permissions = Set.copyOf(permissions); // immutable set
    }

}
