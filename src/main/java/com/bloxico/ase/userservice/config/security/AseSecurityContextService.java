package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AseSecurityContextService {

    @Autowired
    private UserRepository userRepository;

    public Long getPrincipalId() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            var details = auth.getDetails();
            return details instanceof Long
                    ? (Long) details
                    : Long.valueOf((Integer) details);
        }
        return null;
    }

    public User getPrincipal() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            var principal = auth.getPrincipal();
            if(principal instanceof AsePrincipal) {
                var aseUser= (AsePrincipal) principal;
                return aseUser.getUser();
            } else {
                var aseUser = (String) principal;
                return userRepository.findByEmailIgnoreCase(aseUser).orElseThrow(() -> new UsernameNotFoundException(aseUser));
            }

        }
        return null;
    }
}
