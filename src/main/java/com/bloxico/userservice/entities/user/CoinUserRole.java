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
@EqualsAndHashCode(of = {"coinRole"}, callSuper = false)
@Table(name = "users_roles_old")
@ToString(exclude = "coinUser")
public class CoinUserRole extends BaseEntity {

    public CoinUserRole() {
        super();
    }

    public CoinUserRole(CoinUser coinUser, CoinRole coinRole) {
        this.coinUser = coinUser;
        this.coinRole = coinRole;
    }

    @ManyToOne
    @JoinColumn(name = "coin_user_id")
    private CoinUser coinUser;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private CoinRole coinRole;
}
