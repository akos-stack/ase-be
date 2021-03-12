package com.bloxico.ase.userservice.web.model.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SystemConstantsResponse {

    @JsonProperty("constants")
    Map<String, Object> constants;

}
