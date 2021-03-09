package com.bloxico.ase.userservice.entity.token;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "value", callSuper = false)
@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken extends BaseEntity {

    @Column(name = "value")
    private String value;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

}
