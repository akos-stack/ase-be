package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "token", callSuper = false)
@Entity
@Table(name = "blacklisted_jwts")
public class BlacklistedJwt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

}
