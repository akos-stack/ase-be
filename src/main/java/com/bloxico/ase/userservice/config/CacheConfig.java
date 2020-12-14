package com.bloxico.ase.userservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String
            PERMISSION_NAME_GRANTED_AUTHORITIES_MAP_CACHE
            = "permissionNameGrantedAuthoritiesMapCache";

    public static final String
            BLACKLISTED_TOKENS_CACHE
            = "blacklistedTokensCache";

    @Bean
    public CaffeineCache permissionNameGrantedAuthoritiesMapCache() {
        return new CaffeineCache(
                PERMISSION_NAME_GRANTED_AUTHORITIES_MAP_CACHE,
                Caffeine.newBuilder().build());
    }

    @Bean
    public CaffeineCache blacklistedTokensCache() {
        return new CaffeineCache(
                BLACKLISTED_TOKENS_CACHE,
                Caffeine.newBuilder().build());
    }

}
