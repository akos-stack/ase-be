package com.bloxico.ase.userservice.web.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateUserProfileRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("first_name")
    String firstName;

    @NotNull
    @NotEmpty
    @JsonProperty("last_name")
    String lastName;

    @JsonProperty("phone")
    String phone;

}
