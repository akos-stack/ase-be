package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.*;
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
import com.bloxico.ase.userservice.web.model.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.MailUtil.Template.EVALUATOR_INVITATION;
import static com.bloxico.ase.userservice.util.MailUtil.Template.VERIFICATION;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.MATCH_REGISTRATION_PASSWORD_ERROR;

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
        if (!request.isPasswordMatching())
            throw MATCH_REGISTRATION_PASSWORD_ERROR.newException();
        var userDto = MAPPER.toUserDto(request);
        userDto.addRole(rolePermissionService.findRoleByName(request.getRole()));
        userDto = userService.saveUser(userDto);
        var tokenDto = registrationTokenService.createTokenForUser(userDto.getId());
        mailUtil.sendTokenEmail(VERIFICATION, userDto.getEmail(), tokenDto.getValue());
        var response = new RegistrationResponse(tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.registerUserWithVerificationToken - end | request: {}", request);
        return response;
    }

    @Override
    public void handleTokenValidation(TokenValidationRequest request) {
        log.info("UserRegistrationFacadeImpl.handleTokenValidation - start | request: {}", request);
        var token = registrationTokenService.consumeToken(request.getTokenValue());
        userService.enableUser(token.getUserId(), token.getUserId());
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
    public ArtOwnerDto submitArtOwner(SubmitArtOwnerRequest request) {
        log.info("UserRegistrationFacadeImpl.submitArtOwner - start | request: {}", request);
        var artOwnerDto = doSaveArtOwner(request);
        var userId = artOwnerDto.getUserProfile().getUserId();
        var tokenDto = registrationTokenService.createTokenForUser(userId);
        mailUtil.sendTokenEmail(VERIFICATION, request.getEmail(), tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.submitArtOwner - end | request: {}", request);
        return artOwnerDto;
    }

    @Override
    public void requestEvaluatorRegistration(EvaluatorRegistrationRequest request, long principalId) {
        log.info("UserRegistrationFacadeImpl.requestEvaluatorRegistration - start | request: {}, principalId: {}", request, principalId);
        pendingEvaluatorService.createPendingEvaluator(request, principalId);
        log.info("UserRegistrationFacadeImpl.requestEvaluatorRegistration - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public PagedPendingEvaluatorDataResponse searchPendingEvaluators(String email, int page, int size, String sort) {
        log.info("UserRegistrationFacadeImpl.searchPendingEvaluators - start | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        var pendingEvaluators = pendingEvaluatorService.searchPendingEvaluators(email, page, size, sort);
        var response = new PagedPendingEvaluatorDataResponse(pendingEvaluators.getContent(), pendingEvaluators.getContent().size(), pendingEvaluators.getTotalElements(), pendingEvaluators.getTotalPages());
        log.info("UserRegistrationFacadeImpl.searchPendingEvaluators - end | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        return response;
    }

    @Override
    public ByteArrayResource downloadEvaluatorResume(String email, long principalId) {
        log.info("UserRegistrationFacadeImpl.downloadEvaluatorResume - start | email: {}, principalId: {}", email, principalId);
        var response = pendingEvaluatorService.getEvaluatorResume(email, principalId);
        log.info("UserRegistrationFacadeImpl.downloadEvaluatorResume - end | email: {}, principalId: {}", email, principalId);
        return response;
    }

    // HELPER METHODS

    private CountryDto doSaveCountry(ISubmitUserProfileRequest request, long principalId) {
        var countryDto = MAPPER.toCountryDto(request);
        return locationService.findOrSaveCountry(countryDto, principalId);
    }

    private CityDto doSaveCity(CountryDto countryDto, ISubmitUserProfileRequest request, long principalId) {
        var cityDto = MAPPER.toCityDto(request);
        cityDto.setCountry(countryDto);
        return locationService.findOrSaveCity(cityDto, principalId);
    }

    private LocationDto doSaveLocation(ISubmitUserProfileRequest request, long principalId) {
        var countryDto = doSaveCountry(request, principalId);
        var cityDto = doSaveCity(countryDto, request, principalId);
        var locationDto = MAPPER.toLocationDto(request);
        locationDto.setCity(cityDto);
        return locationService.saveLocation(locationDto, principalId);
    }

    private UserDto doSaveUser(ISubmitUserProfileRequest request) {
        var userDto = MAPPER.toUserDto(request);
        userDto.addRole(rolePermissionService.findRoleByName(request.getRole()));
        return userService.saveUser(userDto);
    }

    private UserProfileDto doSaveUserProfile(ISubmitUserProfileRequest request) {
        var principalId = doSaveUser(request).getId();
        var locationDto = doSaveLocation(request, principalId);
        var userProfileDto = MAPPER.toUserProfileDto(request);
        userProfileDto.setUserId(principalId);
        userProfileDto.setLocation(locationDto);
        return userProfileService.saveUserProfile(userProfileDto, principalId);
    }

    private ArtOwnerDto doSaveArtOwner(SubmitArtOwnerRequest request) {
        var userProfileDto = doSaveUserProfile(request);
        var artOwnerDto = MAPPER.toArtOwnerDto(request);
        artOwnerDto.setUserProfile(userProfileDto);
        return userProfileService.saveArtOwner(artOwnerDto, userProfileDto.getUserId());
    }

    private EvaluatorDto doSaveEvaluator(SubmitEvaluatorRequest request) {
        var userProfileDto = doSaveUserProfile(request);
        var evaluatorDto = MAPPER.toEvaluatorDto(request);
        evaluatorDto.setUserProfile(userProfileDto);
        return userProfileService.saveEvaluator(evaluatorDto, userProfileDto.getUserId());
    }

}
