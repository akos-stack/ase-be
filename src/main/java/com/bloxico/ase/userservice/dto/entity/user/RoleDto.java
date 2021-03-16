package com.bloxico.ase.userservice.dto.entity.user;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class RoleDto extends BaseEntityDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("permissions")
    private Set<PermissionDto> permissions;

}
