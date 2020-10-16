package com.bloxico.userservice.entities.user;

import com.bloxico.userservice.entities.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = {"email", "password", "locked", "enabled", "deleted"}, callSuper = false)
@ToString(exclude = {"userRoles", "password"})
@Table(name = "coin_users")

public class CoinUser extends BaseEntity {

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, mappedBy = "coinUser")
    private List<UserRole> userRoles;

    @OneToOne(mappedBy = "coinUser", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.EAGER)
    private UserProfile userProfile;


    @Column(name = "locked")
    private boolean locked;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "deleted")
    private boolean deleted;
}
