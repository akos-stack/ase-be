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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Value
public class AsePrincipal implements UserDetails, OAuth2User {

    private static final long serialVersionUID = 1L;

    UserProfile userProfile;
    Map<String, Object> attributes;

    private AsePrincipal(UserProfile userProfile, Map<String, Object> attributes) {
        this.userProfile = requireNonNull(userProfile);
        this.attributes = Map.copyOf(requireNonNull(attributes));
    }

    public static AsePrincipal newUserDetails(UserProfile userProfile) {
        return new AsePrincipal(userProfile, Map.of());
    }

    public static AsePrincipal newOAuth2User(UserProfile userProfile, Map<String, Object> attributes) {
        return new AsePrincipal(userProfile, attributes);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return userProfile
                .getRoles()
                .stream()
                .map(Role::getName)
                .map(AsePrincipal::authorityOf)
                .collect(toUnmodifiableSet());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
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
