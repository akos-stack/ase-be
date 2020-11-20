package com.bloxico.ase.userservice.web.model.registration;

import com.bloxico.userservice.util.validator.email.ValidEmail;
import com.bloxico.userservice.util.validator.password.RegularPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@ToString(exclude = {"password", "matchPassword"})
public class RegistrationRequest {

    @NotNull
    @NotEmpty
    @ValidEmail
    @JsonProperty("email")
    String email;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("password")
    String password;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("match_password")
    String matchPassword;

    public boolean isPasswordMatching() {
        //noinspection ConstantConditions
        return password.equals(matchPassword);
    }

    public String extractNameFromEmail() {
        //noinspection ConstantConditions
        return email.split("@")[0];
    }

}
