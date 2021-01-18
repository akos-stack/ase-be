package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_EXISTS;
import static javax.persistence.EnumType.STRING;

@Data
@EqualsAndHashCode(of = "token", callSuper = false)
@Entity
@Table(name = "pending_evaluators")
public class PendingEvaluator extends BaseEntity {

    public enum Status {

        INVITED,
        REQUESTED;

        public PendingEvaluator requireDifferentStatus(PendingEvaluator check) {
            if (check.getStatus() == this || (this == REQUESTED && check.getStatus() == INVITED))
                throw TOKEN_EXISTS.newException();
            return check;
        }

    }

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

}
