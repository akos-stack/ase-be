package com.bloxico.userservice.repository.oauth;

import com.bloxico.userservice.entities.oauth.OauthAccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AccessTokenRepository extends JpaRepository<OauthAccessTokenEntity, String> {

    @Modifying
    @Query("delete FROM OauthAccessTokenEntity e WHERE e.expiration < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

}
