package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.util.Cookies;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static com.bloxico.ase.userservice.config.security.CookieOAuthRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.bloxico.ase.userservice.config.security.CookieOAuthRequestRepository.removeAuthorizationRequestCookies;

@Slf4j
@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${oauth2.client.id}")
    private String clientId;

    private final Gson gson;
    private final ClientDetailsService clientDetailsService;
    private final UserProfileRepository userProfileRepository;
    private final CookieOAuthRequestRepository requestRepository;
    private final PersistentJwtTokenStore persistentJwtTokenStore;
    private final JwtAccessTokenConverter tokenEnhancer;

    @Autowired
    public OAuthSuccessHandler(Gson gson, ClientDetailsService clientDetailsService, UserProfileRepository userProfileRepository, CookieOAuthRequestRepository requestRepository, PersistentJwtTokenStore persistentJwtTokenStore, JwtAccessTokenConverter tokenEnhancer) {
        this.gson = gson;
        this.clientDetailsService = clientDetailsService;
        this.userProfileRepository = userProfileRepository;
        this.requestRepository = requestRepository;
        this.persistentJwtTokenStore = persistentJwtTokenStore;
        this.tokenEnhancer = tokenEnhancer;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {
        var targetUrl = determineTargetUrl(request, authentication);
        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to: " + targetUrl);
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
        OAuth2AccessToken token = authenticateSsoUser(authentication.getName());
        var redirectUri = Cookies
                .getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(OAuthSuccessHandler::getAndValidateRedirectUri)
                .orElse(super.getDefaultTargetUrl());
        return UriComponentsBuilder
                .fromUriString(redirectUri) // TODO !!!!!
                .queryParam("token", gson.toJson(token)
                        /*tokenStore. tokenProvider.createToken(authentication)*/)
                .build()
                .toUriString();
    }

    public OAuth2AccessToken authenticateSsoUser(String email) {
        log.debug("Authenticating integrated user - start, email: {}", email);

        UserProfile coinUser = getUserProfile(email);
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        TokenRequest tokenRequest = generateImplicitTokenRequest(coinUser.getEmail());

        OAuth2AccessToken token = getImplicitAccessToken(coinUser, clientDetails, tokenRequest);

        log.debug("Authenticating integrated user - end , token created");
        return token;
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

    private OAuth2AccessToken getImplicitAccessToken(UserProfile userProfile, ClientDetails clientDetails, TokenRequest tokenRequest) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(persistentJwtTokenStore);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancer);

        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication(userProfile, clientDetails, tokenRequest);
        return tokenServices.createAccessToken(oAuth2Authentication);
    }

    private OAuth2Authentication getOAuth2Authentication(UserProfile userProfile, ClientDetails clientDetails, TokenRequest tokenRequest) {
        DefaultOAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
        OAuth2Request storedOAuth2Request = requestFactory.createOAuth2Request(clientDetails, tokenRequest);

        return new OAuth2Authentication(storedOAuth2Request,
                new UsernamePasswordAuthenticationToken(AsePrincipal.newUserDetails(userProfile), null));
    }

    private UserProfile getUserProfile(String email) {
        Optional<UserProfile> op = userProfileRepository.findByEmailIgnoreCase(email);

        return op.orElseThrow(EntityNotFoundException::new);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        removeAuthorizationRequestCookies(request, response);
    }

    private static String getAndValidateRedirectUri(Cookie cookie) {
        var value = cookie.getValue();
        if (!isAuthorizedRedirectUri(value))
            // TODO BadRequestException
            throw new RuntimeException("Unauthorized Redirect URI");
        return value;
    }

    private static boolean isAuthorizedRedirectUri(String uri) {
        var clientRedirectUri = URI.create(uri);
        // TODO fetch authorizedRedirectUris from database
        return List.of("http://localhost:8089/api/")
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    var authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI
                            .getHost()
                            .equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI
                            .getPort() == clientRedirectUri.getPort();
                });
    }

}
