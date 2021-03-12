package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.web.model.ResponsePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchUsersResponse {

    @JsonProperty("page")
    ResponsePage<UserDto> page;

    public SearchUsersResponse(Page<UserDto> page) {
        this.page = new ResponsePage<>(page);
    }

}
