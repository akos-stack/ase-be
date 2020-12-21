package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.util.Cookies;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.bloxico.ase.userservice.util.Cookies.deleteCookie;
import static com.bloxico.ase.userservice.util.Cookies.newCookie;

/**
 * By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
 * the authorization request. But, since our service is stateless, we can't save it in
 * the session. We'll save the request in a Base64 encoded cookie instead.
 */
@Component
public class CookieOAuth2RequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    private static final int cookieExpireSeconds = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return Cookies
                .getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> Cookies.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response)
    {
        if (authorizationRequest == null) {
            deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            return;
        }
        var stateCookie = newCookie(
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                Cookies.serialize(authorizationRequest),
                cookieExpireSeconds);
        response.addCookie(stateCookie);

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (!redirectUriAfterLogin.isBlank()) {
            var redirectUriCookie = newCookie(
                    REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin,
                    cookieExpireSeconds);
            response.addCookie(redirectUriCookie);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return loadAuthorizationRequest(request);
    }

    public static void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }

}