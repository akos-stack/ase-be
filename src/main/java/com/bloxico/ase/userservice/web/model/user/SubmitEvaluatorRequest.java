package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.validator.ValidEmail;
import com.bloxico.ase.userservice.validator.RegularPassword;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

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
public class SubmitEvaluatorRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("token")
    @ApiModelProperty(required = true)
    String token;

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
    @JsonProperty("city")
    @ApiModelProperty(required = true)
    String city;

    @JsonProperty("zip_code")
    String zipCode;

    @NotNull
    @NotEmpty
    @JsonProperty("address")
    @ApiModelProperty(required = true)
    String address;

    @JsonProperty("latitude")
    BigDecimal latitude;

    @JsonProperty("longitude")
    BigDecimal longitude;

}
