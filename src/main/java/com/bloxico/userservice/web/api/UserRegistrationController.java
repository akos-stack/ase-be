package com.bloxico.userservice.web.api;

import com.bloxico.userservice.facade.IRegistrationFacade;
import com.bloxico.userservice.web.model.registration.RegistrationDataResponse;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.userservice.web.model.token.ResendTokenRequest;
import com.bloxico.userservice.web.model.token.TokenValidityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class UserRegistrationController implements UserRegistrationApi {

    private IRegistrationFacade registrationFacade;

    @Autowired
    public UserRegistrationController(IRegistrationFacade registrationFacade) {
        this.registrationFacade = registrationFacade;
    }

    @Override
    public ResponseEntity<Void> registerCoinUser(@Valid @RequestBody RegistrationRequest registrationRequest, HttpServletRequest request) {

        registrationFacade.registerUserWithVerificationToken(registrationRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resendRegistrationMail(@Valid @RequestBody TokenValidityRequest tokenValidityRequest, HttpServletRequest request) {
        registrationFacade.handleTokenValidation(tokenValidityRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> refreshVerificationToken(String token, HttpServletRequest request) {

        registrationFacade.refreshExpiredToken(token);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resendRegistrationMail(@Valid @RequestBody ResendTokenRequest resendTokenRequest, HttpServletRequest request) {
        registrationFacade.resendVerificationToken(resendTokenRequest.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RegistrationDataResponse> getRegistrationData(HttpServletRequest request) {
        RegistrationDataResponse registrationDataResponse = registrationFacade.returnRegistrationData();

        return new ResponseEntity<>(registrationDataResponse, HttpStatus.OK);
    }
}
