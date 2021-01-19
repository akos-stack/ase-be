package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.UserProfileDto;
import com.bloxico.ase.userservice.facade.IUserRegistrationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.token.IPendingEvaluatorService;
import com.bloxico.ase.userservice.service.token.ITokenService;
import com.bloxico.ase.userservice.service.token.impl.RegistrationTokenServiceImpl;
import com.bloxico.ase.userservice.service.user.*;
import com.bloxico.ase.userservice.util.MailUtil;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.MailUtil.Template.EVALUATOR_INVITATION;
import static com.bloxico.ase.userservice.util.MailUtil.Template.VERIFICATION;

@Slf4j
@Service
@Transactional
public class UserRegistrationFacadeImpl implements IUserRegistrationFacade {

    private final IUserService userService;
    private final IUserProfileService userProfileService;
    private final ILocationService locationService;
    private final ITokenService registrationTokenService;
    private final IPendingEvaluatorService pendingEvaluatorService;
    private final IRolePermissionService rolePermissionService;
    private final MailUtil mailUtil;

    @Autowired
    public UserRegistrationFacadeImpl(IUserService userService,
                                      IUserProfileService userProfileService,
                                      ILocationService locationService,
                                      RegistrationTokenServiceImpl registrationTokenService,
                                      IPendingEvaluatorService pendingEvaluatorService,
                                      IRolePermissionService rolePermissionService,
                                      MailUtil mailUtil)
    {
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.locationService = locationService;
        this.registrationTokenService = registrationTokenService;
        this.pendingEvaluatorService = pendingEvaluatorService;
        this.rolePermissionService = rolePermissionService;
        this.mailUtil = mailUtil;
    }

    @Override
    public RegistrationResponse registerUserWithVerificationToken(RegistrationRequest request) {
        log.info("UserRegistrationFacadeImpl.registerUserWithVerificationToken - start | request: {}", request);
        var userDto = userService.saveDisabledUser(request);
        var tokenDto = registrationTokenService.createTokenForUser(userDto.getId());
        mailUtil.sendTokenEmail(VERIFICATION, userDto.getEmail(), tokenDto.getValue());
        var response = new RegistrationResponse(tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.registerUserWithVerificationToken - end | request: {}", request);
        return response;
    }

    @Override
    public void handleTokenValidation(TokenValidationRequest request) {
        log.info("UserRegistrationFacadeImpl.handleTokenValidation - start | request: {}", request);
        var userDto = userService.findUserByEmail(request.getEmail());
        registrationTokenService.consumeTokenForUser(request.getTokenValue(), userDto.getId());
        userService.enableUser(userDto.getId());
        log.info("UserRegistrationFacadeImpl.handleTokenValidation - end | request: {}", request);
    }

    @Override
    public void refreshExpiredToken(String expiredTokenValue) {
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - start | expiredTokenValue: {}", expiredTokenValue);
        var tokenDto = registrationTokenService.refreshToken(expiredTokenValue);
        var userDto = userService.findUserById(tokenDto.getId());
        mailUtil.sendTokenEmail(VERIFICATION, userDto.getEmail(), tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - end | expiredTokenValue: {}", expiredTokenValue);
    }

    @Override
    public void resendVerificationToken(ResendTokenRequest request) {
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - start | request: {}", request);
        var userDto = userService.findUserByEmail(request.getEmail());
        var tokenDto = registrationTokenService.getTokenByUserId(userDto.getId());
        mailUtil.sendTokenEmail(VERIFICATION, request.getEmail(), tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - end | request: {}", request);
    }

    @Override
    public void sendEvaluatorInvitation(EvaluatorInvitationRequest request, long principalId) {
        log.info("UserRegistrationFacadeImpl.sendEvaluatorInvitation - start | request: {}, principalId: {}", request, principalId);
        var token = pendingEvaluatorService.createPendingEvaluator(request, principalId).getToken();
        mailUtil.sendTokenEmail(EVALUATOR_INVITATION, request.getEmail(), token);
        log.info("UserRegistrationFacadeImpl.sendEvaluatorInvitation - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public void checkEvaluatorInvitation(String token) {
        log.info("UserRegistrationFacadeImpl.checkEvaluatorInvitation - start | token: {}", token);
        pendingEvaluatorService.checkInvitationToken(token);
        log.info("UserRegistrationFacadeImpl.checkEvaluatorInvitation - end | token: {}", token);
    }

    @Override
    public void resendEvaluatorInvitation(EvaluatorInvitationResendRequest request) {
        log.info("UserRegistrationFacadeImpl.resendEvaluatorInvitation - start | request: {}", request);
        var token = pendingEvaluatorService.getPendingEvaluatorToken(request.getEmail());
        mailUtil.sendTokenEmail(EVALUATOR_INVITATION, request.getEmail(), token);
        log.info("UserRegistrationFacadeImpl.resendEvaluatorInvitation - end | request: {}", request);
    }

    @Override
    public void withdrawEvaluatorInvitation(EvaluatorInvitationWithdrawalRequest request) {
        log.info("UserRegistrationFacadeImpl.withdrawEvaluatorInvitation - start | request: {}", request);
        pendingEvaluatorService.deletePendingEvaluator(request.getEmail());
        log.info("UserRegistrationFacadeImpl.withdrawEvaluatorInvitation - end | request: {}", request);
    }

    @Override
    public EvaluatorDto submitEvaluator(SubmitEvaluatorRequest request) {
        log.info("UserRegistrationFacadeImpl.submitEvaluator - start | request: {}", request);
        pendingEvaluatorService.consumePendingEvaluator(request.getEmail(), request.getToken());
        var evaluatorDto = doSaveEvaluator(request);
        log.info("UserRegistrationFacadeImpl.submitEvaluator - end | request: {}", request);
        return evaluatorDto;
    }

    @Override
    public void requestEvaluatorRegistration(EvaluatorRegistrationRequest request, long principalId) {
        log.info("UserRegistrationFacadeImpl.requestEvaluatorRegistration - start | request: {}, principalId: {}", request, principalId);
        pendingEvaluatorService.createPendingEvaluator(request, principalId);
        log.info("UserRegistrationFacadeImpl.requestEvaluatorRegistration - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public ArrayPendingEvaluatorDataResponse searchPendingEvaluators(String email, int page, int size, String sort) {
        log.info("UserRegistrationFacadeImpl.searchPendingEvaluators - start | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        var pendingEvaluatorDtos = pendingEvaluatorService.searchPendingEvaluators(email, page, size, sort);
        var response = new ArrayPendingEvaluatorDataResponse(pendingEvaluatorDtos);
        log.info("UserRegistrationFacadeImpl.searchPendingEvaluators - end | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        return response;
    }

    // HELPER METHODS

    private EvaluatorDto doSaveEvaluator(SubmitEvaluatorRequest request) {
        var principalId = doSaveUser(request, EVALUATOR).getId();
        var locationDto = doSaveLocation(request, principalId);
        var userProfileDto = doSaveUserProfile(locationDto, request, principalId);
        var evaluatorDto = MAPPER.toEvaluatorDto(request);
        evaluatorDto.setUserProfile(userProfileDto);
        return userProfileService.saveEvaluator(evaluatorDto, principalId);
    }

    private UserDto doSaveUser(SubmitEvaluatorRequest request, String role) {
        var userDto = MAPPER.toUserDto(request);
        userDto.addRole(rolePermissionService.findRoleByName(role));
        return userService.saveEnabledUser(userDto);
    }

    private LocationDto doSaveLocation(SubmitEvaluatorRequest request, long principalId) {
        var countryDto = doSaveCountry(request, principalId);
        var cityDto = doSaveCity(countryDto, request, principalId);
        var locationDto = MAPPER.toLocationDto(request);
        locationDto.setCity(cityDto);
        return locationService.saveLocation(locationDto, principalId);
    }

    private CountryDto doSaveCountry(SubmitEvaluatorRequest request, long principalId) {
        var countryDto = MAPPER.toCountryDto(request);
        return locationService.findOrSaveCountry(countryDto, principalId);
    }

    private CityDto doSaveCity(CountryDto countryDto, SubmitEvaluatorRequest request, long principalId) {
        var cityDto = MAPPER.toCityDto(request);
        cityDto.setCountry(countryDto);
        return locationService.findOrSaveCity(cityDto, principalId);
    }

    private UserProfileDto doSaveUserProfile(LocationDto locationDto, SubmitEvaluatorRequest request, long principalId) {
        var userProfileDto = MAPPER.toUserProfileDto(request);
        userProfileDto.setUserId(principalId);
        userProfileDto.setLocation(locationDto);
        return userProfileService.saveUserProfile(userProfileDto, principalId);
    }

}
