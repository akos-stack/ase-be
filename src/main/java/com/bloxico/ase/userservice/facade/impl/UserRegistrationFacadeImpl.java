package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.user.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
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

    private final IUserProfileService userProfileService;
    private final IUserRegistrationService userRegistrationService;
    private final ILocationService locationService;
    private final ITokenService registrationTokenService;
    private final IPendingEvaluatorService pendingEvaluatorService;
    private final IRolePermissionService rolePermissionService;
    private final MailUtil mailUtil;

    @Autowired
    public UserRegistrationFacadeImpl(IUserProfileService userProfileService,
                                      IUserRegistrationService userRegistrationService,
                                      ILocationService locationService,
                                      RegistrationTokenServiceImpl registrationTokenService,
                                      IPendingEvaluatorService pendingEvaluatorService,
                                      IRolePermissionService rolePermissionService,
                                      MailUtil mailUtil)
    {
        this.userProfileService = userProfileService;
        this.userRegistrationService = userRegistrationService;
        this.locationService = locationService;
        this.registrationTokenService = registrationTokenService;
        this.pendingEvaluatorService = pendingEvaluatorService;
        this.rolePermissionService = rolePermissionService;
        this.mailUtil = mailUtil;
    }

    @Override
    public RegistrationResponse registerUserWithVerificationToken(RegistrationRequest request) {
        log.info("UserRegistrationFacadeImpl.registerUserWithVerificationToken - start | request: {}", request);
        var userProfileDto = userRegistrationService.registerDisabledUser(request);
        var tokenDto = registrationTokenService.createTokenForUser(userProfileDto.getId());
        mailUtil.sendTokenEmail(VERIFICATION, userProfileDto.getEmail(), tokenDto.getValue());
        var response = new RegistrationResponse(tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.registerUserWithVerificationToken - end | request: {}", request);
        return response;
    }

    @Override
    public void handleTokenValidation(TokenValidationRequest request) {
        log.info("UserRegistrationFacadeImpl.handleTokenValidation - start | request: {}", request);
        var userProfileDto = userProfileService.findUserProfileByEmail(request.getEmail());
        registrationTokenService.consumeTokenForUser(request.getTokenValue(), userProfileDto.getId());
        userRegistrationService.enableUser(userProfileDto.getId());
        log.info("UserRegistrationFacadeImpl.handleTokenValidation - end | request: {}", request);
    }

    @Override
    public void refreshExpiredToken(String expiredTokenValue) {
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - start | expiredTokenValue: {}", expiredTokenValue);
        var tokenDto = registrationTokenService.refreshToken(expiredTokenValue);
        var userProfileDto = userProfileService.findUserProfileById(tokenDto.getId());
        mailUtil.sendTokenEmail(VERIFICATION, userProfileDto.getEmail(), tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - end | expiredTokenValue: {}", expiredTokenValue);
    }

    @Override
    public void resendVerificationToken(ResendTokenRequest request) {
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - start | request: {}", request);
        var userProfileDto = userProfileService.findUserProfileByEmail(request.getEmail());
        var tokenDto = registrationTokenService.getTokenByUserId(userProfileDto.getId());
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
        var userProfileDto = obtainUserProfileDto(request);
        var principalId = userProfileDto.getId();
        var evaluatorDto = MAPPER.toEvaluatorDto(request);
        evaluatorDto.setUserProfile(userProfileDto);
        evaluatorDto = userProfileService.saveEvaluator(evaluatorDto, principalId);
        log.info("UserRegistrationFacadeImpl.submitEvaluator - end | request: {}", request);
        return evaluatorDto;
    }

    private UserProfileDto obtainUserProfileDto(SubmitEvaluatorRequest request) {
        var userProfileDto = MAPPER.toUserProfileDto(request);
        userProfileDto.addRole(rolePermissionService.findRoleByName(EVALUATOR));
        userProfileDto = userProfileService.saveEnabledUserProfile(userProfileDto);
        var locationDto = obtainLocationDto(request, userProfileDto.getId());
        return userProfileService.updateLocation(userProfileDto, locationDto);
    }

    private LocationDto obtainLocationDto(SubmitEvaluatorRequest request, long principalId) {
        var countryDto = MAPPER.toCountryDto(request);
        countryDto = locationService.findOrSaveCountry(countryDto, principalId);
        var cityDto = MAPPER.toCityDto(request);
        cityDto.setCountry(countryDto);
        cityDto = locationService.findOrSaveCity(cityDto, principalId);
        var locationDto = MAPPER.toLocationDto(request);
        locationDto.setCity(cityDto);
        return locationService.saveLocation(locationDto, principalId);
    }

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

}
