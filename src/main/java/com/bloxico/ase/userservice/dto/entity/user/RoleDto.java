package com.bloxico.ase.userservice.dto.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableList;

@Data
@EqualsAndHashCode(of = "name")
public class RoleDto {

    private Short id;
    private String name;
    private Set<PermissionDto> permissions;

    public List<String> getPermissionNames() {
        return permissions
                .stream()
                .map(PermissionDto::getName)
                .collect(toUnmodifiableList());
    }

}
