package com.bloxico.ase.userservice.entity.user;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "name")
@Table(name = "roles")
@Entity
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ADMIN = "admin";
    public static final String USER = "user";
    public static final String EVALUATOR = "evaluator";

    @Getter // Note: Currently not used
    public enum Value {

        ADMIN("admin"),
        USER("user"),
        EVALUATOR("evaluator");

        private final String name;

        Value(String name) {
            this.name = name;
        }

        public List<Value> allRoles() {
            return List.of(values());
        }

    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Short id;

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
