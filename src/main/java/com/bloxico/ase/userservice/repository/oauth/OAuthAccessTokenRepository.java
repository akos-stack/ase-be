package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OAuthAccessTokenRepository extends JpaRepository<OAuthAccessToken, String> {

    List<OAuthAccessTokenDto> findAllByUserNameIgnoreCase(String email);

    @Modifying
    void deleteByUserNameIgnoreCase(String email);

    @Modifying
    @Query("delete FROM OauthAccessTokenEntity e WHERE e.expiration < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

}
