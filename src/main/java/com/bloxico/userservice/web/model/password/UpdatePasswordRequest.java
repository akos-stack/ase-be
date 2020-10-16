package com.bloxico.userservice.web.model.password;

import com.bloxico.userservice.util.validator.password.RegularPassword;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data

public class UpdatePasswordRequest {

    @NotNull
    private String oldPassword;

    @NotNull
    @RegularPassword
    private String newPassword;
}
