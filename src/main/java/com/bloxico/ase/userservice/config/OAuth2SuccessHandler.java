package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.util.Cookies;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.bloxico.ase.userservice.config.CookieOAuth2RequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.bloxico.ase.userservice.config.CookieOAuth2RequestRepository.removeAuthorizationRequestCookies;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenStore tokenStore;
    private final CookieOAuth2RequestRepository requestRepository;

    @Autowired
    public OAuth2SuccessHandler(TokenStore tokenStore, CookieOAuth2RequestRepository requestRepository) {
        this.tokenStore = tokenStore;
        this.requestRepository = requestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException
    {
        var targetUrl = determineTargetUrl(request, authentication);
        if (!response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to: " + targetUrl);
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
        var redirectUri = Cookies
                .getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(OAuth2SuccessHandler::getAndValidateRedirectUri)
                .orElse(super.getDefaultTargetUrl());
        return UriComponentsBuilder
                .fromUriString(redirectUri) // TODO !!!!!
                .queryParam("token", "TODO Authentication->JWT"
                        /*tokenStore. tokenProvider.createToken(authentication)*/)
                .build()
                .toUriString();
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
        return List.of("http://localhost:8089/api/oauth2/redirect")
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
