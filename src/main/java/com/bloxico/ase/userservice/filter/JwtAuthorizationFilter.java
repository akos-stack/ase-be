package com.bloxico.ase.userservice.filter;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.service.token.IJwtService;
import com.bloxico.ase.userservice.web.api.AuthenticationApi;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private IJwtService jwtService;
    TokenStore tokenStore;

    public JwtAuthorizationFilter(IJwtService jwtService, TokenStore tokenStore) {
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
    }

    //HINT JWT Verification is done here
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException
    {

        var uri = request.getRequestURI().replace(request.getContextPath(), "");
//        if (URI_PERMISSION_MAP.containsKey(uri)) {
        if (true) {
            var header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer "))
                throw ErrorCodes.Jwt.INVALID_TOKEN.newException();

            var jwt = header.substring(7);
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(jwt);

            var decodedJwt = jwtService.verifyToken(jwt);
            request.setAttribute("decodedJwt", decodedJwt);

            SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);
        }
        chain.doFilter(request, response);
    }

}
