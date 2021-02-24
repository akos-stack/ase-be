package com.bloxico.ase.userservice.web.model.registration;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.validator.RegularPassword;
import com.bloxico.ase.userservice.validator.ValidEmail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@ToString(exclude = {"password", "matchPassword"})
@Setter
@Getter
public class RegistrationRequest{

    @JsonIgnore
    public String getRole() {
        return Role.USER;
    }

    @JsonIgnore
    public boolean getEnabled() {
        return false;
    }

    @NotNull
    @NotEmpty
    @ValidEmail
    @JsonProperty("email")
    @ApiModelProperty(required = true)
    String email;

    @NotNull
    @NotEmpty
    @JsonProperty("username")
    @ApiModelProperty(required = true)
    String username;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("password")
    @ApiModelProperty(required = true)
    String password;

    @NotNull
    @NotEmpty
    @RegularPassword
    @JsonProperty("match_password")
    @ApiModelProperty(required = true)
    String matchPassword;

    @JsonProperty("aspirations")
    Set<String> aspirationNames;

//    @Nullable
//    @JsonProperty("image")
//    @ApiModelProperty(required = false)
//    MultipartFile image;

    @JsonIgnore
    public boolean isPasswordMatching() {
        //noinspection ConstantConditions
        return password.equals(matchPassword);
    }

//    public RegistrationRequest(String email, String username,
//                               String password, String matchPassword, Set<String> aspirationNames) {
//        this.email = email;
//        this.username = username;
//        this.password = password;
//        this.matchPassword = matchPassword;
//        this.aspirationNames = aspirationNames;
//    }
}
