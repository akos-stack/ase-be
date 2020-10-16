package com.bloxico.userservice.entities.token;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@DiscriminatorValue("password_reset_token")
public class PasswordResetToken extends Token {

}