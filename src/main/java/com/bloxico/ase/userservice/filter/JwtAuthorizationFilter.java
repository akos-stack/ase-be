package com.bloxico.ase.userservice.filter;

import com.bloxico.ase.userservice.service.token.IJwtService;
import com.bloxico.ase.userservice.web.api.AuthenticationApi;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private IJwtService jwtService;

    private static final Map<String, String>
            URI_PERMISSION_MAP
            = Map.ofEntries(entry(AuthenticationApi.BLACKLIST_ENDPOINT, "BLACKLIST"));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException
    {
        var uri = request.getRequestURI().replace(request.getContextPath(), "");
        if (URI_PERMISSION_MAP.containsKey(uri)) {
            var header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer "))
                throw ErrorCodes.Jwt.INVALID_TOKEN.newException();
            var decodedJwt = jwtService.verifyToken(header.substring(7));
            var permission = URI_PERMISSION_MAP.get(uri);
            if (permission != null && !decodedJwt.getPermissions().contains(permission))
                throw ErrorCodes.Jwt.INVALID_TOKEN.newException();
            request.setAttribute("decodedJwt", decodedJwt);
        }
        chain.doFilter(request, response);
    }

}
