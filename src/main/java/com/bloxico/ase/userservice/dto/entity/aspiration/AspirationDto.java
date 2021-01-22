package com.bloxico.ase.userservice.dto.entity.aspiration;

import com.bloxico.ase.userservice.dto.entity.user.RoleDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AspirationDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("role")
    private RoleDto role;

}
