package com.bloxico.ase.userservice.web.model.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class HostInvitationWithdrawalRequest {

    @NotNull
    @JsonProperty("user_id")
    @ApiModelProperty(required = true)
    Long userId;

}
