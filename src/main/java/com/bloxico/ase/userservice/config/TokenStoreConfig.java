package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.entity.user.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.LinkedHashMap;

import static java.util.stream.Collectors.toList;

@Configuration
public class TokenStoreConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${schema.name}")
    private String schema;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public DataSource dataSource() {
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setSchema(schema);
        return dataSource;
    }

    @Bean
    protected JwtAccessTokenConverter jwtAccessTokenConverter() {
        var converter = new CustomTokenEnhancer();
        converter.setSigningKey(jwtSecret);
        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new PersistentJwtTokenStore(dataSource(), jwtAccessTokenConverter());
    }

    private static class CustomTokenEnhancer extends JwtAccessTokenConverter {
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            var userDetails = (AsePrincipal) authentication.getPrincipal();

            var info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
            info.put("id", userDetails.getUserProfile().getId());
            info.put("roles", userDetails.getUserProfile().getRoles().stream().map(Role::getName).collect(toList()));

            var customAccessToken = new DefaultOAuth2AccessToken(accessToken);
            customAccessToken.setAdditionalInformation(info);

            return super.enhance(customAccessToken, authentication);
        }
    }

}
