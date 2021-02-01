package com.bloxico.ase.userservice.web.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ISubmitUserProfileRequest {

    String getRole();

    boolean getEnabled();

    String getUsername();

    String getPassword();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getPhone();

    LocalDate getBirthday();

    String getGender();

    String getCountry();

    String getCity();

    String getZipCode();

    String getAddress();

    BigDecimal getLatitude();

    BigDecimal getLongitude();

}
