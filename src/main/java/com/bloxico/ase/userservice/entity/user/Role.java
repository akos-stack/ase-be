package com.bloxico.ase.userservice.entity.user;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
@Table(name = "roles")
@Entity
public class Role extends BaseEntity {

    public static final String ADMIN = "admin";
    public static final String USER = "user";
    public static final String EVALUATOR = "evaluator";
    public static final String ART_OWNER = "art_owner";

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = EAGER, cascade = MERGE)
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

}
