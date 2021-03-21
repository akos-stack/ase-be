package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.validator.RegularPassword;
import com.bloxico.ase.userservice.validator.ValidEmail;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@ToString(exclude = "password")
public class SubmitArtOwnerRequest implements ISubmitUserProfileRequest {

    @JsonIgnore
    @Override
    public String getRole() {
        return Role.ART_OWNER;
    }

    @JsonIgnore
    @Override
    public boolean getEnabled() {
        return false;
    }

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
    @ValidEmail
    @JsonProperty("email")
    @ApiModelProperty(required = true)
    String email;

    @NotNull
    @NotEmpty
    @JsonProperty("first_name")
    @ApiModelProperty(required = true)
    String firstName;

    @NotNull
    @NotEmpty
    @JsonProperty("last_name")
    @ApiModelProperty(required = true)
    String lastName;

    @JsonProperty("phone")
    String phone;

    @NotNull
    @JsonProperty("birthday")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    // TODO replace with default LocalDate serde config
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @ApiModelProperty(required = true)
    LocalDate birthday;

    @JsonProperty("gender")
    String gender;

    @NotNull
    @NotEmpty
    @JsonProperty("country")
    @ApiModelProperty(required = true)
    String country;

    @NotNull
    @NotEmpty
    @JsonProperty("address")
    @ApiModelProperty(required = true)
    String address;

    @JsonProperty("latitude")
    BigDecimal latitude;

    @JsonProperty("longitude")
    BigDecimal longitude;

    @Nullable
    @JsonProperty("profile_image")
    MultipartFile profileImage;

}
