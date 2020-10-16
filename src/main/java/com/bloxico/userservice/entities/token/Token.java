package com.bloxico.userservice.entities.token;

import com.bloxico.userservice.entities.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@EqualsAndHashCode(of = {"tokenValue"}, callSuper = false)
@DiscriminatorColumn(name = "token_type", discriminatorType = DiscriminatorType.STRING)
@ToString
@Table(name = "tokens")
@Data
public class Token extends BaseEntity {

    @Column(name = "token_value", nullable = false)
    private String tokenValue;

    @Column(name = "coin_user_id")
    private Long userId;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

}