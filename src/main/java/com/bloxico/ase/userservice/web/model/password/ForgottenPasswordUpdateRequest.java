package com.bloxico.ase.userservice.web.model.password;

import com.bloxico.ase.userservice.validator.ValidEmail;
import com.bloxico.ase.userservice.validator.RegularPassword;
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
@ToString(exclude = "newPassword")
public class ForgottenPasswordUpdateRequest {

    @NotNull
    @NotEmpty
    @ValidEmail
    @JsonProperty("email")
    @ApiModelProperty(required = true)
    String email;

    @NotNull
    @NotEmpty
    @JsonProperty("token_value")
    @ApiModelProperty(required = true)
    String tokenValue;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("new_password")
    @ApiModelProperty(required = true)
    String newPassword;

}
