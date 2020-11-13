package com.bloxico.ase.userservice.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
@ToString(exclude = "permissions")
@Table(name = "roles")
@Entity
public class Role {

    public static final String BLACKLIST = "BLACKLIST";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Short id;

    @Column(name = "name")
    private String name;

    @ManyToMany(cascade = {PERSIST, MERGE})
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    public GrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + name);
    }

}
