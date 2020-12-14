package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Value
public class AseUserDetails implements UserDetails, OAuth2User {

    private static final long serialVersionUID = 1L;

    UserProfile userProfile;
    Map<String, Object> attributes;

    public AseUserDetails(UserProfile userProfile) {
        this.userProfile = userProfile;
        attributes = Map.of();
    }

    public AseUserDetails(UserProfile userProfile, Map<String, Object> attributes) {
        this.userProfile = userProfile;
        this.attributes = Map.copyOf(attributes);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return userProfile
                .getRoles()
                .stream()
                .map(Role::getName)
                .map(AseUserDetails::authorityOf)
                .collect(toUnmodifiableSet());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getPassword() {
        return userProfile.getPassword();
    }

    @Override
    public String getUsername() {
        return userProfile.getEmail();
    }

    @Override
    public String getName() {
        // TODO is it ok?
        return userProfile.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !userProfile.getLocked();
    }

    @Override
    public boolean isEnabled() {
        return userProfile.getEnabled();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public static GrantedAuthority authorityOf(String roleName) {
        return new SimpleGrantedAuthority("ROLE_" + roleName);
    }

}
