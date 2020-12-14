package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.config.AseUserDetails;
import com.bloxico.ase.userservice.config.ExternalUserInfoExtractor;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service // TODO Composition instead of inheritance
public class UserProfileServiceImpl extends DefaultOAuth2UserService implements IUserProfileService, UserDetailsService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfileDto findUserProfileById(long id) {
        log.debug("UserProfileServiceImpl.findUserProfileById - start | id: {}", id);
        var userProfileDto = userProfileRepository
                .findById(id)
                .map(MAPPER::toDto)
                .orElseThrow(ErrorCodes.User.USER_NOT_FOUND::newException);
        log.debug("UserProfileServiceImpl.findUserProfileById - end | id: {}", id);
        return userProfileDto;
    }

    @Override
    public UserProfileDto findUserProfileByEmail(String email) {
        log.debug("UserProfileServiceImpl.findUserByEmail - start | email: {}", email);
        requireNonNull(email);
        var userProfileDto = userProfileRepository
                .findByEmailIgnoreCase(email)
                .map(MAPPER::toDto)
                .orElseThrow(ErrorCodes.User.USER_NOT_FOUND::newException);
        log.debug("UserProfileServiceImpl.findUserByEmail - end | email: {}", email);
        return userProfileDto;
    }

    @Override
    public UserProfileDto updateUserProfile(long id, UpdateUserProfileRequest request) {
        log.debug("UserProfileServiceImpl.updateUserProfile - start | id: {}, request: {}", id, request);
        requireNonNull(request);
        var userProfile = userProfileRepository
                .findById(id)
                .orElseThrow(ErrorCodes.User.USER_NOT_FOUND::newException);
        userProfile.setName(request.getName());
        userProfile.setPhone(request.getPhone());
        userProfile.setUpdaterId(id);
        userProfile = userProfileRepository.saveAndFlush(userProfile);
        var userProfileDto = MAPPER.toDto(userProfile);
        log.debug("UserProfileServiceImpl.updateUserProfile - end | id: {}, request: {}", id, request);
        return userProfileDto;
    }

    @Override
    public void disableUser(long userId, long principalId) {
        log.debug("UserProfileServiceImpl.disableUser - start | userId: {}, principalId: {}", userId, principalId);
        var userProfile = userProfileRepository
                .findById(userId)
                .orElseThrow(ErrorCodes.User.USER_NOT_FOUND::newException);
        userProfile.setEnabled(false);
        userProfile.setUpdaterId(principalId);
        userProfileRepository.saveAndFlush(userProfile);
        log.debug("UserProfileServiceImpl.disableUser - end | userId: {}, principalId: {}", userId, principalId);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.debug("UserProfileServiceImpl.loadUserByUsername - start | email: {}", email);
        requireNonNull(email);
        var userProfile = userProfileRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        var aseUserDetails = new AseUserDetails(userProfile);
        log.debug("UserProfileServiceImpl.loadUserByUsername - end | email: {}", email);
        return aseUserDetails;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        log.debug("UserProfileServiceImpl.loadUser - start | request: {}", request);
        var oAuth2User = super.loadUser(request);
        try {
            var provider = request.getClientRegistration().getRegistrationId();
            var extractor = ExternalUserInfoExtractor.of(provider);
            var attributes = oAuth2User.getAttributes();
            var email = extractor.getEmail(attributes);
            if (email == null || email.isBlank())
                // TODO extend
                throw new RuntimeException("Email not found from OAuth2 provider");
            // TODO refactor provider and disabled checking
            var userProfile = userProfileRepository
                    .findByEmailIgnoreCase(email)
                    .map(user -> extractor.updateUserProfile(user, attributes, provider))
                    .orElseGet(() -> extractor.newUserProfile(attributes));
            // TODO check if changed?
            userProfile = userProfileRepository.saveAndFlush(userProfile);
            var aseOauth2User = new AseUserDetails(userProfile, attributes);
            log.debug("UserProfileServiceImpl.loadUser - end | request: {}", request);
            return aseOauth2User;
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Trigger OAuth2AuthenticationFailureHandler with this exception
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

}
