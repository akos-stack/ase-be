package com.bloxico.ase.userservice.dto.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(of = "email")
public class UserProfileDto {

    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private RoleDto role;

    public List<String> getPermissionNames() {
        return role.getPermissionNames();
    }

}
