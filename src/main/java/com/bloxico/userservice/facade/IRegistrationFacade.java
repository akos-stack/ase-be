package com.bloxico.userservice.facade;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.web.model.registration.RegistrationDataResponse;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.userservice.web.model.token.TokenValidityRequest;

public interface IRegistrationFacade {

    CoinUserDto registerUserWithVerificationToken(RegistrationRequest registrationRequest);

    void handleTokenValidation(TokenValidityRequest tokenValidityRequest);

    void refreshExpiredToken(String expiredTokenValue);

    void resendVerificationToken(String email);

    RegistrationDataResponse returnRegistrationData();
}
