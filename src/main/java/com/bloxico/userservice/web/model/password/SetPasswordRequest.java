package com.bloxico.userservice.web.model.password;

import com.bloxico.userservice.util.validator.password.RegularPassword;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SetPasswordRequest {

    @RegularPassword
    @NotNull
    private String password;
}
