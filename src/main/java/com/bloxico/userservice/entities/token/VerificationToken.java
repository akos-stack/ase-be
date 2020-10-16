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
@DiscriminatorValue("verification_token")
public class VerificationToken extends Token {
}