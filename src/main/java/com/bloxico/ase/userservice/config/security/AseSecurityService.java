package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepository;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class AseSecurityService extends DefaultOAuth2UserService
        implements UserDetailsService, ClientDetailsService
{

    private final UserRepository userRepository;
    private final OAuthClientDetailsRepository oAuthClientDetailsRepository;

    @Autowired
    public AseSecurityService(UserRepository userRepository,
                              OAuthClientDetailsRepository oAuthClientDetailsRepository)
    {
        this.userRepository = userRepository;
        this.oAuthClientDetailsRepository = oAuthClientDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.debug("AseSecurityService.loadUserByUsername - start | email: {}", email);
        requireNonNull(email);
        var user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        var aseUserDetails = AsePrincipal.newUserDetails(user);
        log.debug("AseSecurityService.loadUserByUsername - end | email: {}", email);
        return aseUserDetails;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) {
        log.debug("AseSecurityService.loadUser - start | request: {}", request);
        requireNonNull(request);
        var oAuth2User = super.loadUser(request);
        try {
            var provider = request.getClientRegistration().getRegistrationId();
            var extractor = ExternalUserDataExtractor.of(provider);
            var attributes = oAuth2User.getAttributes();
            var email = extractor.getEmail(attributes);
            if (email == null || email.isBlank())
                throw new InternalAuthenticationServiceException(
                        "Email not found from OAuth2 provider");
            var user = userRepository
                    .findByEmailIgnoreCase(email)
                    .map(extractor::validateUser)
                    .map(u -> extractor.updateUser(u, attributes))
                    .orElseGet(() -> extractor.newUser(attributes));
            user = userRepository.saveAndFlush(user);
            var aseOauth2User = AsePrincipal.newOAuth2User(user, attributes);
            log.debug("AseSecurityService.loadUser - end | request: {}", request);
            return aseOauth2User;
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // AuthenticationServiceException triggers OAuth2FailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        log.debug("AseSecurityService.loadClientByClientId - start | clientId: {}", clientId);
        requireNonNull(clientId);
        var dto = oAuthClientDetailsRepository
                .findByClientId(clientId)
                .orElseThrow(() -> new ClientRegistrationException(clientId));
        var bcd = new BaseClientDetails();
        bcd.setClientId(dto.getClientId());
        bcd.setClientSecret(dto.getClientSecret());
        bcd.setScope(dto.scopesAsSet());
        bcd.setAuthorizedGrantTypes(dto.authorizedGrantTypesAsSet());
        bcd.setAuthorities(dto.authoritiesAsGrantedAuthoritiesSet());
        bcd.setAccessTokenValiditySeconds(dto.getAccessTokenValidity());
        log.debug("AseSecurityService.loadClientByClientId - end | clientId: {}", clientId);
        return bcd;
    }

}
