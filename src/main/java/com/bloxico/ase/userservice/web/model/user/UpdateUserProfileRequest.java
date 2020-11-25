package com.bloxico.ase.userservice.web.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateUserProfileRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("name")
    String name;

    @JsonProperty("phone")
    String phone;

}
