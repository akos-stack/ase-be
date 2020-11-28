package com.bloxico.userservice.config.oauth2;

import com.bloxico.ase.userservice.config.AseUserDetails;
import com.bloxico.ase.userservice.config.PersistentJwtTokenStore;
import com.bloxico.ase.userservice.entity.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Configuration
@Slf4j
public class TokenStoreConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        String driverClassName = env.getProperty("spring.datasource.driver-class-name");
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setSchema(env.getProperty("schema.name"));

        return dataSource;
    }


    //HINT: Beans below are used to create JWT token from standard uuid access token

    //Token store is used to convert jwt token (string) into Authentication object
    //that will be put into security context
    @Bean
    public TokenStore tokenStore() {
        return new PersistentJwtTokenStore(dataSource(), jwtAccessTokenConverter());
    }

    @Bean
    protected JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new CustomTokenEnhancer();
        converter.setSigningKey(jwtSecret);
        return converter;
    }

    //store custom stuff into JWT token here
    protected static class CustomTokenEnhancer extends JwtAccessTokenConverter {
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            var userDetails = (AseUserDetails) authentication.getPrincipal();

            Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());

            info.put("id", userDetails.getUserProfile().getId());
            info.put("roles", userDetails.getUserProfile().getRoles().stream().map(Role::getName).collect(toList()));

            DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
            customAccessToken.setAdditionalInformation(info);

            return super.enhance(customAccessToken, authentication);
        }
    }

//    @Bean
//    public TokenStore tokenStore() {
//        return new CustomJdbcTokenStore(dataSource());
//    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource());
    }
}
