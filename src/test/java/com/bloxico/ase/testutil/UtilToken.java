package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.token.TokenDto;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.facade.impl.UserManagementFacadeImpl;
import com.bloxico.ase.userservice.facade.impl.UserRegistrationFacadeImpl;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import com.bloxico.ase.userservice.repository.token.*;
import com.bloxico.ase.userservice.service.token.impl.PendingEvaluatorServiceImpl;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static com.bloxico.ase.userservice.util.SupportedFileExtension.pdf;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;

@Component
public class UtilToken {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private TokenRepository tokenRepository;
    @Autowired private OAuthAccessTokenRepository oAuthAccessTokenRepository;
    @Autowired private BlacklistedTokenRepository blacklistedTokenRepository;
    @Autowired private PendingEvaluatorRepository pendingEvaluatorRepository;
    @Autowired private PendingEvaluatorServiceImpl pendingEvaluatorService;
    @Autowired private UserManagementFacadeImpl userManagementFacade;
    @Autowired private UserRegistrationFacadeImpl userRegistrationFacade;

    public Token savedToken(Token.Type type) {
        return savedToken(type, genUUID());
    }

    public Token savedToken(Token.Type type, String value) {
        var adminId = utilUser.savedAdmin().getId();
        var token = new Token();
        token.setUserId(adminId);
        token.setValue(value);
        token.setType(type);
        token.setExpiryDate(genNonExpiredLDT());
        token.setCreatorId(adminId);
        return tokenRepository.saveAndFlush(token);
    }

    public Token savedExpiredToken(Token.Type type) {
        return savedExpiredToken(type, genUUID());
    }

    public Token savedExpiredToken(Token.Type type, String value) {
        var userId = utilUser.savedUser().getId();
        var token = new Token();
        token.setUserId(userId);
        token.setValue(value);
        token.setType(type);
        token.setExpiryDate(genExpiredLDT());
        token.setCreatorId(userId);
        return tokenRepository.saveAndFlush(token);
    }

    public TokenDto savedExpiredTokenDto(Token.Type type) {
        return MAPPER.toDto(savedExpiredToken(type));
    }

    public OAuthAccessToken savedOauthToken(String email) {
        var token = new OAuthAccessToken();
        token.setTokenId(genUUID());
        token.setUserName(email);
        token.setExpiration(genNonExpiredLDT());
        return oAuthAccessTokenRepository.saveAndFlush(token);
    }

    public OAuthAccessTokenDto savedOauthTokenDto(String email) {
        return MAPPER.toDto(savedOauthToken(email));
    }

    public OAuthAccessToken savedExpiredOauthToken(String email) {
        var token = new OAuthAccessToken();
        token.setTokenId(genUUID());
        token.setUserName(email);
        token.setExpiration(genExpiredLDT());
        return oAuthAccessTokenRepository.saveAndFlush(token);
    }

    public OAuthAccessTokenDto savedExpiredOauthTokenDto(String email) {
        return MAPPER.toDto(savedExpiredOauthToken(email));
    }

    public BlacklistedToken getBlacklistedToken(String value) {
        return blacklistedTokenRepository
                .findAll()
                .stream()
                .filter(t -> t.getValue().equals(value))
                .findAny()
                .orElseThrow();
    }

    public BlacklistedToken savedBlacklistedToken() {
        var user = utilUser.savedUser();
        var token = savedOauthTokenDto(user.getEmail());
        userManagementFacade.blacklistTokens(user.getId());
        return getBlacklistedToken(token.getTokenId());
    }

    public BlacklistedToken savedExpiredBlacklistedToken() {
        var user = utilUser.savedUser();
        var token = savedExpiredOauthTokenDto(user.getEmail());
        userManagementFacade.blacklistTokens(user.getId());
        return getBlacklistedToken(token.getTokenId());
    }

    public OAuthAccessTokenDto toOAuthAccessTokenDto(User user, String token) {
        return toOAuthAccessTokenDto(user.getEmail(), token);
    }

    public OAuthAccessTokenDto toOAuthAccessTokenDto(String email, String token) {
        return new OAuthAccessTokenDto(
                token.contains("Bearer ") ? token.replace("Bearer ", "") : token,
                null, null,
                email,
                "appId",
                null,
                genNonExpiredLDT(),
                genUUID());
    }

    public boolean isEvaluatorPending(String email) {
        return pendingEvaluatorRepository
                .findByEmailIgnoreCase(email)
                .isPresent();
    }

    public PendingEvaluatorDto savedInvitedPendingEvaluatorDto() {
        return savedInvitedPendingEvaluatorDto(genEmail());
    }

    public PendingEvaluatorDto savedInvitedPendingEvaluatorDto(String email) {
        var request = new EvaluatorInvitationRequest(email);
        return pendingEvaluatorService.createPendingEvaluator(MAPPER.toPendingEvaluatorDto(request));
    }

    public String savedRequestedPendingEvaluatorDto() {
        return savedRequestedPendingEvaluatorDto(genEmail());
    }

    public String savedRequestedPendingEvaluatorDto(String email) {
        var request = new EvaluatorRegistrationRequest(email, genMultipartFile(pdf));
        userRegistrationFacade.requestEvaluatorRegistration(request);
        return email;
    }

    public SubmitEvaluatorRequest submitInvitedEvaluatorRequest() {
        return submitInvitedEvaluatorRequest(utilLocation.savedCountry().getName());
    }

    public SubmitEvaluatorRequest submitInvitedEvaluatorRequest(String country) {
        var email = genEmail();
        var password = genPassword();
        userRegistrationFacade.sendEvaluatorInvitation(new EvaluatorInvitationRequest(email));
        var token = pendingEvaluatorRepository.findByEmailIgnoreCase(email).orElseThrow().getToken();
        return new SubmitEvaluatorRequest(
                token, genUUID(), password,
                email, genUUID(), genUUID(),
                genUUID(), LocalDate.now(),
                genUUID(), country,
                genUUID(), ONE, TEN,
                genMultipartFile(IMAGE));
    }

    public static SearchPendingEvaluatorsRequest genSearchPendingEvaluatorsRequest() {
        return new SearchPendingEvaluatorsRequest(genEmail());
    }

    public static SearchPendingEvaluatorsRequest genSearchPendingEvaluatorsRequest(String email) {
        return new SearchPendingEvaluatorsRequest(email);
    }

}
