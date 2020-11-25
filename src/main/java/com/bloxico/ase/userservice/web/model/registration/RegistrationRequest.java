package com.bloxico.ase.userservice.web.model.registration;

import com.bloxico.userservice.util.validator.email.ValidEmail;
import com.bloxico.userservice.util.validator.password.RegularPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(required = true)
    String email;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("password")
    @ApiModelProperty(required = true)
    String password;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("match_password")
    @ApiModelProperty(required = true)
    String matchPassword;

    @JsonIgnore
    public boolean isPasswordMatching() {
        //noinspection ConstantConditions
        return password.equals(matchPassword);
    }

    public String extractNameFromEmail() {
        //noinspection ConstantConditions
        return email.split("@")[0];
    }

}
