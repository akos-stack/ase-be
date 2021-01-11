package com.bloxico.ase.userservice.web.model.password;

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
@ToString(exclude = {"oldPassword", "newPassword"})
public class KnownPasswordUpdateRequest {

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("old_password")
    @ApiModelProperty(required = true)
    String oldPassword;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("new_password")
    @ApiModelProperty(required = true)
    String newPassword;

}
