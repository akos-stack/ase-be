package com.bloxico.ase.userservice.filter;

import com.bloxico.ase.userservice.service.token.IJwtService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final TokenStore tokenStore;

    public JwtAuthorizationFilter(IJwtService jwtService, TokenStore tokenStore) {
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
    }

    //HINT JWT Verification is done here
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException
    {
        var header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            raiseException(response);
            return;
        }
        try {
            var jwt = header.substring(7);
            var decodedJwt = jwtService.verifyToken(jwt);

            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(jwt);
            oAuth2Authentication.setDetails(decodedJwt.getUserId());
            SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);

            chain.doFilter(request, response);
        } catch (Exception e) {
            raiseException(response);
        }
    }

    private static void raiseException(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        var status = ErrorCodes.Jwt.INVALID_TOKEN.getHttpStatus();
        var code = ErrorCodes.Jwt.INVALID_TOKEN.getCode();
        ApiError apiError = new ApiError(status, code);
        byte[] body = new ObjectMapper().writeValueAsBytes(apiError);
        response.getOutputStream().write(body);
    }

}
