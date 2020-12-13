package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "value", callSuper = false)
@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "value")
    private String value;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

}
