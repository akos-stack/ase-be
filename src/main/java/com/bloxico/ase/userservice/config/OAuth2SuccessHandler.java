package com.bloxico.ase.userservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

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
    {

    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        // TODO temp
        return List.of("http://localhost:8089/oauth2/redirect")
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI
                            .getHost()
                            .equalsIgnoreCase(clientRedirectUri.getHost())
                        && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }

}
