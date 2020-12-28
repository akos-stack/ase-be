package com.bloxico.ase.userservice.web.model.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@ToString(exclude = "password")
public class SubmitEvaluatorRequest {

    String token;

    // UserProfile
    String username;
    String password;
    String email;
    String firstName;
    String lastName;
    String phone;
    LocalDate birthday;
    String gender;

    // Location
    String country;
    String city;
    String zipCode;
    String address;
    BigDecimal latitude;
    BigDecimal longitude;

}
