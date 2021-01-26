package com.bloxico.ase.userservice.web.model.token;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.List;

@Value
public class PagedPendingEvaluatorDataResponse {

    @JsonProperty("pending_evaluators")
    @ApiModelProperty(required = true)
    List<PendingEvaluatorDto> pendingEvaluators;

    @JsonProperty("pageSize")
    @ApiModelProperty(required = true)
    long pageElements;

    @JsonProperty("totalElements")
    @ApiModelProperty(required = true)
    long totalElements;

    @JsonProperty("totalPages")
    @ApiModelProperty(required = true)
    long totalPages;
}
