package com.bloxico.userservice.entities.user;

import com.bloxico.userservice.entities.BaseEntity;
import com.bloxico.userservice.entities.user.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = {"roleName"}, callSuper = false)
@Table(name = "roles")
public class Role extends BaseEntity {

    public enum RoleName {
        USER, ADMIN
    }

    public Role() {
        super();
    }

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role_name", nullable = false)
    private RoleName roleName;

    @OneToMany
    @JoinColumn(name = "role_id")
    private List<UserRole> userRoles;
}
