package com.bloxico.ase.userservice.web.model.password;

import com.bloxico.userservice.util.validator.password.RegularPassword;
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
@ToString(exclude = "password")
public class SetPasswordRequest {

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("password")
    @ApiModelProperty(required = true)
    String password;

}
