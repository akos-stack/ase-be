package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(of = "token", callSuper = false)
@Entity
@Table(name = "pending_evaluators")
public class PendingEvaluator extends BaseEntity {

    @Id
    @Column(name = "token")
    private String token;

    @Column(name = "email")
    private String email;

}
