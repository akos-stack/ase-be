package com.bloxico.userservice.config.oauth2;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Types;

// DEPRECATED
/**
 * This extension is required in order to add expiration field inside access token table, since JdbcTokenStore does not provide this feature.
 */
//@Component
public class CustomJdbcTokenStore extends JdbcTokenStore {

    private final JdbcTemplate jdbcTemplate;
    private static final String EXPIRY_ACCESS_TOKEN_INSERT_STATEMENT = "insert into oauth_access_token (token_id, token, authentication_id, user_name, client_id, authentication, refresh_token, expiration) values (?, ?, ?, ?, ?, ?, ?, ?)";
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();


    public CustomJdbcTokenStore(DataSource dataSource) {
        super(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);

    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (readAccessToken(token.getValue()) != null) {
            removeAccessToken(token.getValue());
        }

        jdbcTemplate.update(EXPIRY_ACCESS_TOKEN_INSERT_STATEMENT, new Object[]{extractTokenKey(token.getValue()),
                new SqlLobValue(serializeAccessToken(token)), authenticationKeyGenerator.extractKey(authentication),
                authentication.isClientOnly() ? null : authentication.getName(),
                authentication.getOAuth2Request().getClientId(),
                new SqlLobValue(serializeAuthentication(authentication)), extractTokenKey(refreshToken), token.getExpiration()}, new int[]{
                Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.TIMESTAMP});
    }
}
