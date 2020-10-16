package com.bloxico.userservice.web.model.password;

import com.bloxico.userservice.util.validator.password.RegularPassword;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString(exclude = "newPassword")
public class ForgotPasswordChangeRequest {

    @NotNull
    private String email;

    @NotNull
    private String tokenValue;

    @NotNull
    @RegularPassword
    private String newPassword;
}
