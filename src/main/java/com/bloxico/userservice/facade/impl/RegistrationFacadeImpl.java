package com.bloxico.userservice.facade.impl;

import com.bloxico.userservice.dto.RegistrationRequestDto;
import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.dto.entities.RegionDto;
import com.bloxico.userservice.dto.entities.TokenDto;
import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.facade.IRegistrationFacade;
import com.bloxico.userservice.services.token.ITokenService;
import com.bloxico.userservice.services.user.IUserRegistrationService;
import com.bloxico.userservice.services.user.IUserService;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImpl;
import com.bloxico.userservice.util.MailUtil;
import com.bloxico.userservice.util.mappers.RegistrationRequestMapper;
import com.bloxico.userservice.web.model.registration.RegistrationDataResponse;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.userservice.web.model.token.TokenValidityRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RegistrationFacadeImpl implements IRegistrationFacade {

    private IUserService userService;
    private IUserRegistrationService userRegistrationService;
    private ITokenService<VerificationToken> verificationTokenService;
    private MailUtil mailUtil;

    @Autowired
    public RegistrationFacadeImpl(IUserService userService,
                                  IUserRegistrationService userRegistrationService,
                                  VerificationTokenServiceImpl verificationTokenService,
                                  MailUtil mailUtil) {

        this.userService = userService;
        this.userRegistrationService = userRegistrationService;
        this.verificationTokenService = verificationTokenService;
        this.mailUtil = mailUtil;
    }

    @Override
    @Transactional
    public CoinUserDto registerUserWithVerificationToken(RegistrationRequest registrationRequest) {
        log.info("Direct user registration - start , request: {}", registrationRequest);

        RegistrationRequestDto registrationRequestDto = RegistrationRequestMapper.INSTANCE.requestToDto(registrationRequest);

        CoinUserDto coinUserDto = userRegistrationService.registerDisabledUser(registrationRequestDto);

        TokenDto tokenDto = verificationTokenService.createTokenForUser(coinUserDto.getId());

        mailUtil.sendVerificationTokenEmail(coinUserDto.getEmail(), tokenDto.getTokenValue());

        log.info("Direct user registration - end , email sent to user.");
        return coinUserDto;
    }


    @Override
    public void handleTokenValidation(TokenValidityRequest tokenValidityRequest) {
        log.info("Handle token validation - start, validity request {}", tokenValidityRequest);

        String tokenValue = tokenValidityRequest.getTokenValue();
        String email = tokenValidityRequest.getEmail();

        CoinUserDto coinUserDto = userService.findUserByEmail(email);

        verificationTokenService.consumeTokenForUser(coinUserDto.getId(), tokenValidityRequest.getTokenValue());

        userRegistrationService.enableUser(coinUserDto.getId());

        log.info("Handle token validation - end, found user id {}", coinUserDto.getId());
    }

    @Override
    public void refreshExpiredToken(String expiredTokenValue) {
        log.info("Refreshing expired token - start for value: {}", expiredTokenValue);

        TokenDto tokenDto = verificationTokenService.refreshExpiredToken(expiredTokenValue);

        CoinUserDto coinUserDto = userService.findUserByUserId(tokenDto.getUserId());

        mailUtil.sendVerificationTokenEmail(coinUserDto.getEmail(), tokenDto.getTokenValue());
        log.info("Refreshing expired token - end , email sent to: {}", coinUserDto.getEmail());
    }

    @Override
    public void resendVerificationToken(String email) {
        log.info("Resend verification token - start, email: {}", email);

        CoinUserDto coinUserDto = userService.findUserByEmail(email);

        TokenDto tokenDto = verificationTokenService.getTokenByUserId(coinUserDto.getId());

        mailUtil.sendVerificationTokenEmail(email, tokenDto.getTokenValue());
        log.info("Resend verification token - end, email sent to: {}", email);
    }

    @Override
    public RegistrationDataResponse returnRegistrationData() {
        log.info("Return registration data - start");

        List<RegionDto> regions = userService.getRegionList();

        RegistrationDataResponse registrationDataResponse = new RegistrationDataResponse();
        registrationDataResponse.setRegions(regions);

        log.info("Return registration data - end");
        return registrationDataResponse;
    }


}
