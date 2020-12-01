package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthAccessTokenRepository extends JpaRepository<OAuthAccessToken, String> {
}
