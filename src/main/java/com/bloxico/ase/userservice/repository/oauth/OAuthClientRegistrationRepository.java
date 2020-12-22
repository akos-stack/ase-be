package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthClientRegistrationDto;
import com.bloxico.ase.userservice.entity.oauth.OAuthClientRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthClientRegistrationRepository extends JpaRepository<OAuthClientRegistration, String> {

    Optional<OAuthClientRegistrationDto> findByRegistrationId(String registrationId);

}
