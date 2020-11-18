package com.bloxico.ase.userservice.dto.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = "email")
public class UserProfileDto {

    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private Boolean locked = false;
    private Boolean enabled = false;
    private Set<RoleDto> roles;

    public Stream<String> streamRoleNames() {
        return roles
                .stream()
                .map(RoleDto::getName);
    }

}
