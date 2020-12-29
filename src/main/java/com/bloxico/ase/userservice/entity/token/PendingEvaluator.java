package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Data
@EqualsAndHashCode(of = "token", callSuper = false)
@Entity
@Table(name = "pending_evaluators")
public class PendingEvaluator extends BaseEntity {

    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "token")
    private String token;

    @Column(name = "status")
    @Enumerated(STRING)
    private Status status;

    @Column(name = "cv_path")
    private String cvPath;

    public enum Status {

        INVITED,
        REQUESTED

    }

}
