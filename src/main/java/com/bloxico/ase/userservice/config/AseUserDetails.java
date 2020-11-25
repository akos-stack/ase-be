package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Value
public class AseUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    UserProfileDto userProfile;

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return userProfile
                .streamRoleNames()
                .map(AseUserDetails::authorityOf)
                .collect(toUnmodifiableSet());
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
