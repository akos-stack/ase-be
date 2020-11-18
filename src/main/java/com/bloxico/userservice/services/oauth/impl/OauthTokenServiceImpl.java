package com.bloxico.userservice.services.oauth.impl;

import com.bloxico.ase.userservice.config.AseUserDetails;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.userservice.config.oauth2.CoinClientDetailsService;
import com.bloxico.userservice.config.oauth2.CustomJdbcTokenStore;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.exceptions.OAuthTokenServiceException;
import com.bloxico.userservice.repository.oauth.AccessTokenRepository;
import com.bloxico.userservice.repository.user.CoinUserRepository;
import com.bloxico.userservice.services.oauth.IOauthTokenService;
import com.bloxico.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Service
@Slf4j
public class OauthTokenServiceImpl implements IOauthTokenService {

    @Value("${oauth2.client.id}")
    private String clientId;

    @Value("${authorization.endpoint.url}")
    private String authServiceUrl;

    private AccessTokenRepository accessTokenRepository;
    private CoinUserRepository coinUserRepository;
    private CoinClientDetailsService coinClientDetailsService;
    private CustomJdbcTokenStore customJdbcTokenStore;

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public OauthTokenServiceImpl(AccessTokenRepository accessTokenRepository,
                                 CoinUserRepository coinUserRepository,
                                 CoinClientDetailsService coinClientDetailsService,
                                 CustomJdbcTokenStore customJdbcTokenStore,
                                 UserProfileRepository userProfileRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.coinUserRepository = coinUserRepository;
        this.coinClientDetailsService = coinClientDetailsService;
        this.customJdbcTokenStore = customJdbcTokenStore;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public String authenticateIntegratedUser(String email) {
        log.debug("Authenticating integrated user - start, email: {}", email);

        var coinUser = getUserProfile(email); //getCoinUser(email);
        ClientDetails clientDetails = coinClientDetailsService.loadClientByClientId(clientId);
        TokenRequest tokenRequest = generateImplicitTokenRequest(coinUser.getEmail());

        OAuth2AccessToken token = getImplicitAccessToken(coinUser, clientDetails, tokenRequest);

        log.debug("Authenticating integrated user - end , token created");
        return token.getValue();
    }

    @Override
    public String authenticateClientCredentials(String clientId, String secret) {
        log.debug("Authenticating using client credentials - start, client Id: {}", clientId);

        try {
            ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
            resource.setGrantType("client_credentials");
            resource.setAccessTokenUri(authServiceUrl + "/oauth/token");
            resource.setClientId(clientId);
            resource.setClientSecret(secret);

            AccessTokenRequest defaultAccessTokenRequest = new DefaultAccessTokenRequest();

            OAuth2RestTemplate template = new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext(defaultAccessTokenRequest));

            OAuth2AccessToken oAuth2AccessToken = template.getAccessToken();

            log.debug("Authenticating using client credentials - end, client id: {} , token created", clientId);
            return oAuth2AccessToken.getValue();
        } catch (Exception e) {
            log.warn("Problem with client credentials authentication");
            throw new OAuthTokenServiceException(ErrorCodes.CLIENT_CREDENTIALS_AUTH_ERROR.getCode());
        }
    }

    private TokenRequest generateImplicitTokenRequest(String email) {

        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("grant_type", "password");
        requestParameters.put("scope", "access_profile");
        requestParameters.put("email", email);

        List<String> scope = new ArrayList<>();
        scope.add("access_profile");

        String grantType = "password";

        return new TokenRequest(requestParameters, clientId, scope, grantType);
    }

    private OAuth2AccessToken getImplicitAccessToken(UserProfileDto coinUser, ClientDetails clientDetails, TokenRequest tokenRequest) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(customJdbcTokenStore);
        tokenServices.setClientDetailsService(coinClientDetailsService);

        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication(coinUser, clientDetails, tokenRequest);
        return tokenServices.createAccessToken(oAuth2Authentication);
    }

    private OAuth2Authentication getOAuth2Authentication(UserProfileDto coinUser, ClientDetails clientDetails, TokenRequest tokenRequest) {
        DefaultOAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(coinClientDetailsService);
        OAuth2Request storedOAuth2Request = requestFactory.createOAuth2Request(clientDetails, tokenRequest);

        return new OAuth2Authentication(storedOAuth2Request, new UsernamePasswordAuthenticationToken(new AseUserDetails(coinUser), null));
    }

    private CoinUser getCoinUser(String email) {
        Optional<CoinUser> op = coinUserRepository.findByEmailIgnoreCase(email);

        return op.orElseThrow(EntityNotFoundException::new);
    }

    private UserProfileDto getUserProfile(String email) {
        var userProfile = userProfileRepository.findByEmailIgnoreCase(email)
                .orElseThrow(EntityNotFoundException::new);

        return MAPPER.toUserProfileDto(userProfile);
    }

    @Override
    public void deleteExpiredAccessTokens() {
        log.debug("Deleting expired oauth access tokens - start");

        accessTokenRepository.deleteExpiredTokens();

        log.debug("Deleting expired oauth access tokens - end");
    }
}
