package com.bloxico.ase.userservice.web.model.token;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.List;

@Value
public class ArrayPendingEvaluatorDataResponse {

    @JsonProperty("pending_evaluators")
    @ApiModelProperty(required = true)
    List<PendingEvaluatorDto> pendingEvaluators;

}
