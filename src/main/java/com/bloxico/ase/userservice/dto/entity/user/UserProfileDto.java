package com.bloxico.ase.userservice.dto.entity.user;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = "email")
@ToString(exclude = "password")
public class UserProfileDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("password")
    private String password;

    @JsonProperty("email")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("birthday")
    private LocalDate birthday;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("location")
    private LocationDto location;

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

    @JsonProperty("aspirations")
    private Set<String> aspirationNames = new HashSet<>();

    public void addRole(RoleDto role) {
        roles.add(role);
    }

    public Stream<String> streamRoleNames() {
        return roles
                .stream()
                .map(RoleDto::getName);
    }

}
