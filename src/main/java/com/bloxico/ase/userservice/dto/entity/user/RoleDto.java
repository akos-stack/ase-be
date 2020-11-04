package com.bloxico.ase.userservice.dto.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = "name")
public class RoleDto {

    private Short id;
    private String name;
    private Set<PermissionDto> permissions;

    public Stream<String> streamPermissionNames() {
        return permissions
                .stream()
                .map(PermissionDto::getName);
    }

}
