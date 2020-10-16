package com.bloxico.userservice.repository.oauth;

import com.bloxico.userservice.entities.oauth.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientDetailsRepository extends JpaRepository<OauthClientDetails, String> {

    Optional<OauthClientDetails> findByClientId(String clientId);
}
