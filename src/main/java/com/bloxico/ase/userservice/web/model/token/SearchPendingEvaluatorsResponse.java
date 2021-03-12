package com.bloxico.ase.userservice.web.model.token;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.web.model.ResponsePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchPendingEvaluatorsResponse {

    @JsonProperty("page")
    ResponsePage<PendingEvaluatorDto> page;

    public SearchPendingEvaluatorsResponse(Page<PendingEvaluatorDto> page) {
        this.page = new ResponsePage<>(page);
    }

}
