package com.bloxico.ase.userservice.web.model.password;

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
@ToString(exclude = "newPassword")
public class ForgottenPasswordUpdateRequest {

    @NotNull
    @NotEmpty
    @ValidEmail
    @JsonProperty("email")
    String email;

    @NotNull
    @NotEmpty
    @JsonProperty("token_value")
    String tokenValue;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("new_password")
    String newPassword;

}
