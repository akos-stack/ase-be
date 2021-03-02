package com.bloxico.ase.userservice.config.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Optional;

@Configuration
@Profile("!test")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", modifyOnCreate = false)
public class JpaAuditConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
                var details = auth.getDetails();
                return details instanceof Long
                        ? Optional.of((Long) details)
                        : Optional.of(Long.valueOf((Integer) details));
            }
            return Optional.empty();
        };
    }
}
