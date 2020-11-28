package com.bloxico.ase.userservice.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;

import static java.sql.Types.BLOB;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;
import static org.springframework.security.oauth2.common.util.SerializationUtils.serialize;

@Component
public class PersistentJwtTokenStore implements TokenStore {

    private static final String ACCESS_TOKEN_INSERT_STATEMENT
            = "INSERT INTO oauth_access_token " +
            "(token_id, token, authentication_id, user_name, client_id, authentication, refresh_token, expiration) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;
    private final JwtTokenStore jwtTokenStore;
    private final AuthenticationKeyGenerator authenticationKeyGenerator;

    public PersistentJwtTokenStore(DataSource dataSource, JwtAccessTokenConverter converter) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jwtTokenStore = new JwtTokenStore(converter);
        authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication auth) {
        jwtTokenStore.storeAccessToken(token, auth);
        var tokenValue = token.getValue();
        var refreshToken = token.getRefreshToken();
        jdbcTemplate.update(
                ACCESS_TOKEN_INSERT_STATEMENT,
                new Object[]{
                        tokenValue,
                        new SqlLobValue(serialize(token)),
                        authenticationKeyGenerator.extractKey(auth),
                        auth.isClientOnly() ? null : auth.getName(),
                        auth.getOAuth2Request().getClientId(),
                        new SqlLobValue(serialize(auth)),
                        refreshToken == null ? null : refreshToken.getValue(),
                        token.getExpiration()},
                new int[]{VARCHAR, BLOB, VARCHAR, VARCHAR, VARCHAR, BLOB, VARCHAR, TIMESTAMP});
    }

    // Same as org.springframework.security.oauth2.provider.token.store.JwtTokenStore:

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return jwtTokenStore.readAuthentication(token);
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return jwtTokenStore.readAuthentication(token);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return jwtTokenStore.readAccessToken(tokenValue);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        jwtTokenStore.removeAccessToken(token);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        jwtTokenStore.storeRefreshToken(refreshToken, authentication);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return jwtTokenStore.readRefreshToken(tokenValue);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return jwtTokenStore.readAuthenticationForRefreshToken(token);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        jwtTokenStore.removeRefreshToken(token);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        jwtTokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return jwtTokenStore.getAccessToken(authentication);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return jwtTokenStore.findTokensByClientIdAndUserName(clientId, userName);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return jwtTokenStore.findTokensByClientId(clientId);
    }

}
