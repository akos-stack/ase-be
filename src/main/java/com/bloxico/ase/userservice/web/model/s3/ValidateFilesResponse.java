package com.bloxico.ase.userservice.web.model.s3;

import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class ValidateFilesResponse {

    @JsonProperty("errors")
    Map<String, Set<ErrorCodes.AmazonS3>> errors;

}
