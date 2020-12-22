package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.util.Cookies;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.bloxico.ase.userservice.config.security.CookieOAuthRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.bloxico.ase.userservice.config.security.CookieOAuthRequestRepository.removeAuthorizationRequestCookies;

@Component
public class OAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException
    {
        var targetUrl = Cookies
                .getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .map(url -> UriComponentsBuilder
                        .fromUriString(url)
                        .queryParam("error", exception.getLocalizedMessage())
                        .build()
                        .toUriString())
                .orElse(("/"));
        removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
