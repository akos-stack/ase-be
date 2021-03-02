package com.bloxico.ase.userservice.config.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@Profile("test")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", modifyOnCreate = false)
public class JpaAuditTestConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
//            if (SecurityContextHolder.getContext().getAuthentication() != null) {
//                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//                AsePrincipal user = (AsePrincipal) auth.getPrincipal();
//                return Optional.of(user.getUser().getId());
//            }

            return Optional.of(1L);
        };
    }
}
