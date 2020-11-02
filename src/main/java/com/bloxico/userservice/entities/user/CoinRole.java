package com.bloxico.userservice.entities.user;

import com.bloxico.userservice.entities.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = {"roleName"}, callSuper = false)
@Table(name = "roles_old")
public class CoinRole extends BaseEntity {

    public enum RoleName {
        USER, ADMIN
    }

    public CoinRole() {
        super();
    }

    public CoinRole(RoleName roleName) {
        this.roleName = roleName;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role_name", nullable = false)
    private RoleName roleName;

    @OneToMany
    @JoinColumn(name = "role_id")
    private List<CoinUserRole> coinUserRoles;
}
