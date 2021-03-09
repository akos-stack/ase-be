package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntityAudit;
import com.bloxico.ase.userservice.entity.document.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_EXISTS;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;

@Data
@EqualsAndHashCode(of = "token", callSuper = false)
@Entity
@Table(name = "pending_evaluators")
public class PendingEvaluator extends BaseEntityAudit {

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

    @OneToOne(fetch = EAGER, cascade = MERGE)
    @JoinTable(
            name = "pending_evaluators_documents",
            joinColumns = @JoinColumn(name = "email"),
            inverseJoinColumns = @JoinColumn(name = "document_id"))
    private Document document;
}
