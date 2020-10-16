package com.bloxico.userservice.entities.user;

import com.bloxico.userservice.entities.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents Associative table between COIN_USERS and ROLES in the database
 */
@Entity
@Data
@EqualsAndHashCode(of = {"role"}, callSuper = false)
@Table(name = "users_roles")
@ToString(exclude = "coinUser")
public class UserRole extends BaseEntity {

    public UserRole() {
        super();
    }

    public UserRole(CoinUser coinUser, Role role) {
        this.coinUser = coinUser;
        this.role = role;
    }

    @ManyToOne
    @JoinColumn(name = "coin_user_id")
    private CoinUser coinUser;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
