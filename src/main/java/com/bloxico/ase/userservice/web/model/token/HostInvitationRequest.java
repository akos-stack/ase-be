package com.bloxico.ase.userservice.web.model.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class HostInvitationRequest {

    @NotNull
    @JsonProperty("user_id")
    Long userId;

}
