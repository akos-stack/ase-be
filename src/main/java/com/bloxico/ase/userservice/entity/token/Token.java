package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Data
@EqualsAndHashCode(of = "value", callSuper = false)
@Entity
@Table(name = "tokens")
public class Token extends BaseEntity {

    public enum Type {
        REGISTRATION,
        PASSWORD_RESET
    }

    @Column(name = "value")
    private String value;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(now());
    }

}
