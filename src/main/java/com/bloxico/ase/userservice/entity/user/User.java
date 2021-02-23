package com.bloxico.ase.userservice.entity.user;

import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.entity.document.Document;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toSet;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "email", callSuper = false)
@ToString(exclude = {"roles", "aspirations"})
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "locked")
    private Boolean locked = false;

    @Column(name = "enabled")
    private Boolean enabled = false;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @ManyToMany(fetch = EAGER, cascade = MERGE)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
    }

    @ManyToMany(fetch = EAGER, cascade = MERGE)
    @JoinTable(
            name = "users_aspirations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> aspirations = new HashSet<>();

    public void addAspiration(Role aspiration) {
        aspirations.add(aspiration);
    }

    public void addAllAspirations(Collection<Role> aspirations) {
        this.aspirations.addAll(aspirations);
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        setPassword(passwordEncoder.encode(getPassword()));
    }

    // used by AseMapper
    public Set<String> getAspirationNames() {
        return aspirations
                .stream()
                .map(Role::getName)
                .collect(toSet());
    }

    @OneToOne(fetch = EAGER, cascade = MERGE)
    @JoinTable(
            name = "user_profile_image_document",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id"))
    private Document document;

}
