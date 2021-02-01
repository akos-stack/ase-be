package com.bloxico.ase.userservice.dto.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "email")
@ToString(exclude = "password")
public class UserDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("password")
    private String password;

    @JsonProperty("email")
    private String email;

    @JsonProperty("locked")
    private Boolean locked = false;

    @JsonProperty("enabled")
    private Boolean enabled = false;

    @JsonProperty("provider")
    private String provider;

    @JsonProperty("provider_id")
    private String providerId;

    @JsonProperty("roles")
    private Set<RoleDto> roles = new HashSet<>();

    public void addRole(RoleDto role) {
        roles.add(role);
    }

}
