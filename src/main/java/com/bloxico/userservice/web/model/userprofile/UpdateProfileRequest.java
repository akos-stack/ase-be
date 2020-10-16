package com.bloxico.userservice.web.model.userprofile;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateProfileRequest {

    private String name;

    @NotNull
    @NotEmpty
    private String region;

    private String city;

}
