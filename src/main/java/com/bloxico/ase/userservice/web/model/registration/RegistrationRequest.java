package com.bloxico.ase.userservice.web.model.registration;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.validator.RegularPassword;
import com.bloxico.ase.userservice.validator.ValidEmail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@ToString(exclude = {"password", "matchPassword"})
public class RegistrationRequest {

    @JsonIgnore
    public String getRole() {
        return Role.USER;
    }

    @JsonIgnore
    public boolean getEnabled() {
        return false;
    }

    @NotNull
    @NotEmpty
    @ValidEmail
    @JsonProperty("email")
    @ApiModelProperty(required = true)
    String email;

    @NotNull
    @NotEmpty
    @JsonProperty("username")
    @ApiModelProperty(required = true)
    String username;

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

    @JsonProperty("aspirations")
    Set<String> aspirationNames;

    @JsonIgnore
    public boolean isPasswordMatching() {
        //noinspection ConstantConditions
        return password.equals(matchPassword);
    }

}
