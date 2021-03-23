package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.*;
import com.bloxico.ase.userservice.facade.IUserRegistrationFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.service.token.IPendingEvaluatorService;
import com.bloxico.ase.userservice.service.token.ITokenService;
import com.bloxico.ase.userservice.service.token.impl.HostInvitationTokenServiceImpl;
import com.bloxico.ase.userservice.service.token.impl.RegistrationTokenServiceImpl;
import com.bloxico.ase.userservice.service.user.*;
import com.bloxico.ase.userservice.util.MailUtil;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.config.security.AseSecurityContext.getPrincipalId;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static com.bloxico.ase.userservice.util.MailUtil.Template.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_NOT_FOUND;
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
    private final IDocumentService documentService;
    private final ITokenService hostInvitationTokenService;

    @Autowired
    public UserRegistrationFacadeImpl(IUserService userService,
                                      IUserProfileService userProfileService,
                                      ILocationService locationService,
                                      RegistrationTokenServiceImpl registrationTokenService,
                                      IPendingEvaluatorService pendingEvaluatorService,
                                      IRolePermissionService rolePermissionService,
                                      MailUtil mailUtil, IDocumentService documentService,
                                      HostInvitationTokenServiceImpl hostInvitationTokenService)
    {
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.locationService = locationService;
        this.registrationTokenService = registrationTokenService;
        this.pendingEvaluatorService = pendingEvaluatorService;
        this.rolePermissionService = rolePermissionService;
        this.mailUtil = mailUtil;
        this.documentService = documentService;
        this.hostInvitationTokenService = hostInvitationTokenService;
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
    public void refreshExpiredToken(RefreshRegistrationTokenRequest request) {
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - start | request: {}", request);
        var tokenDto = registrationTokenService.refreshToken(request.getToken());
        var userDto = userService.findUserById(tokenDto.getUserId());
        mailUtil.sendTokenEmail(VERIFICATION, userDto.getEmail(), tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - end | request: {}", request);
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
    public void sendEvaluatorInvitation(EvaluatorInvitationRequest request) {
        log.info("UserRegistrationFacadeImpl.sendEvaluatorInvitation - start | request: {}", request);
        var token = pendingEvaluatorService.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request)).getToken();
        mailUtil.sendTokenEmail(EVALUATOR_INVITATION, request.getEmail(), token);
        log.info("UserRegistrationFacadeImpl.sendEvaluatorInvitation - end | request: {}", request);
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
        var profileImage = request.getProfileImage();
        if (profileImage != null) {
            var principalId = evaluatorDto.getUserProfile().getUserId();
            var documentId = documentService.saveDocument(profileImage, IMAGE, principalId).getId();
            var userProfileId = evaluatorDto.getUserProfile().getId();
            userProfileService.saveUserProfileDocument(userProfileId, documentId, principalId);
        }
        log.info("UserRegistrationFacadeImpl.submitEvaluator - end | request: {}", request);
        return evaluatorDto;
    }

    @Override
    public ArtOwnerDto submitArtOwner(SubmitArtOwnerRequest request) {
        log.info("UserRegistrationFacadeImpl.submitArtOwner - start | request: {}", request);
        var artOwnerDto = doSaveArtOwner(request);
        var userId = artOwnerDto.getUserProfile().getUserId();
        var tokenDto = registrationTokenService.createTokenForUser(userId);
        var profileImage = request.getProfileImage();
        if (profileImage != null) {
            var principalId = artOwnerDto.getUserProfile().getUserId();
            var documentId = documentService.saveDocument(profileImage, IMAGE, principalId).getId();
            var userProfileId = artOwnerDto.getUserProfile().getId();
            userProfileService.saveUserProfileDocument(userProfileId, documentId, userId);
        }
        mailUtil.sendTokenEmail(VERIFICATION, request.getEmail(), tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.submitArtOwner - end | request: {}", request);
        return artOwnerDto;
    }

    @Override
    public void requestEvaluatorRegistration(EvaluatorRegistrationRequest request) {
        log.info("UserRegistrationFacadeImpl.requestEvaluatorRegistration - start | request: {}", request);
        var pendingEvaluatorDto = pendingEvaluatorService.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));
        var documentDto = documentService.saveDocument(request.getCv(), CV);
        pendingEvaluatorService.savePendingEvaluatorDocument(pendingEvaluatorDto.getEmail(), documentDto.getId());
        log.info("UserRegistrationFacadeImpl.requestEvaluatorRegistration - end | request: {}", request);
    }

    @Override
    public SearchPendingEvaluatorsResponse searchPendingEvaluators(SearchPendingEvaluatorsRequest request, PageRequest page) {
        log.info("UserRegistrationFacadeImpl.searchPendingEvaluators - start | request: {}, page {}", request, page);
        var result = pendingEvaluatorService.searchPendingEvaluators(request, page);
        var response = new SearchPendingEvaluatorsResponse(result);
        log.info("UserRegistrationFacadeImpl.searchPendingEvaluators - end | request: {}, page {}", request, page);
        return response;
    }

    @Override
    public ByteArrayResource downloadEvaluatorResume(DownloadEvaluatorResumeRequest request) {
        log.info("UserRegistrationFacadeImpl.downloadEvaluatorResume - start | request: {}", request);
        var pendingEvaluatorDocumentDto = pendingEvaluatorService.getEvaluatorResume(request.getEmail());
        var response = documentService.findDocumentById(pendingEvaluatorDocumentDto.getDocumentId());
        log.info("UserRegistrationFacadeImpl.downloadEvaluatorResume - end | request: {}", request);
        return response;
    }

    @Override
    public void sendHostInvitation(HostInvitationRequest request) {
        log.info("UserRegistrationFacadeImpl.sendHostInvitation - start | request: {}", request);
        var userDto = userService.findUserById(request.getUserId());
        hostInvitationTokenService.requireTokenNotExistsForUser(request.getUserId());
        var token = hostInvitationTokenService.createTokenForUser(request.getUserId());
        mailUtil.sendTokenEmail(HOST_INVITATION, userDto.getEmail(), token.getValue());
        log.info("UserRegistrationFacadeImpl.sendHostInvitation - end | request: {}", request);
    }

    @Override
    public void withdrawHostInvitation(HostInvitationWithdrawalRequest request) {
        log.info("UserRegistrationFacadeImpl.withdrawHostInvitation - start | request: {}", request);
        var tokenDto = hostInvitationTokenService.getTokenByUserId(request.getUserId());
        hostInvitationTokenService.consumeToken(tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.withdrawHostInvitation - end | request: {}", request);
    }

    @Override
    public void checkHostInvitation(String token) {
        log.info("UserRegistrationFacadeImpl.checkHostInvitation - start | token: {}", token);
        var tokenDto = hostInvitationTokenService.getTokenByUserId(getPrincipalId());
        if (!tokenDto.getValue().equals(token))
            throw TOKEN_NOT_FOUND.newException();
        log.info("UserRegistrationFacadeImpl.checkHostInvitation - end | token: {}", token);
    }

    @Override
    public void refreshHostInvitationToken(HostInvitationRefreshTokenRequest request) {
        log.info("UserRegistrationFacadeImpl.refreshHostInvitationToken - start | request: {}", request);
        checkHostInvitation(request.getToken());
        var tokenDto = hostInvitationTokenService.refreshToken(request.getToken());
        var userDto = userService.findUserById(tokenDto.getUserId());
        mailUtil.sendTokenEmail(HOST_INVITATION, userDto.getEmail(), tokenDto.getValue());
        log.info("UserRegistrationFacadeImpl.refreshHostInvitationToken - end | request: {}", request);
    }

    // HELPER METHODS

    private LocationDto doSaveLocation(ISubmitUserProfileRequest request, Long principalId) {
        var locationDto = MAPPER.toLocationDto(request);
        locationDto.setCountry(locationService.findCountryByName(request.getCountry()));
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
