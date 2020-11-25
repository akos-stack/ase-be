package com.bloxico.ase.userservice.dto.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name")
public class PermissionDto {

    private Short id;
    private String name;

}
