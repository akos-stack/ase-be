package com.bloxico.ase.userservice.web.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class BlacklistTokensRequest {

    @NotNull
    @JsonProperty("user_id")
    Long userId;

}
