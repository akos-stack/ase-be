package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthClientDetailsDto;
import com.bloxico.ase.userservice.entity.oauth.OAuthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthClientDetailsRepository extends JpaRepository<OAuthClientDetails, String> {

    Optional<OAuthClientDetailsDto> findByClientId(String clientId);

}
