package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(of = "token", callSuper = false)
@Entity
@Table(name = "blacklisted_jwt")
public class BlacklistedJwt extends BaseEntity {

    @Id
    private String token;

}
