package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OAuthAccessTokenRepository extends JpaRepository<OAuthAccessToken, String> {

    List<OAuthAccessTokenDto> findAllByUserNameIgnoreCase(String email);

    @Modifying
    void deleteByUserNameIgnoreCase(String email);

    @Modifying
    @Query("DELETE FROM OAuthAccessToken o WHERE o.expiration < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

}
