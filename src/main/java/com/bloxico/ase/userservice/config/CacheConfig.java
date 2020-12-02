package com.bloxico.ase.userservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CaffeineCache permissionNameGrantedAuthoritiesMapCache() {
        return new CaffeineCache(
                "permissionNameGrantedAuthoritiesMapCache",
                Caffeine.newBuilder().build());
    }

    @Bean
    public CaffeineCache blacklistedTokensCache() {
        return new CaffeineCache(
                "blacklistedTokensCache",
                Caffeine.newBuilder().build());
    }

}
