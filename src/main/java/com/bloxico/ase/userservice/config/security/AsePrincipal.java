package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.User;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Value
public class AsePrincipal implements UserDetails, OAuth2User {

    private static final long serialVersionUID = 1L;

    User user;
    Map<String, Object> attributes;

    private AsePrincipal(User user, Map<String, Object> attributes) {
        this.user = requireNonNull(user);
        //noinspection Java9CollectionFactory
        this.attributes = attributes == null
                ? Map.of()
                // Map.copyOf() doesn't accept null keys
                : unmodifiableMap(new HashMap<>(attributes));
    }

    public static AsePrincipal newUserDetails(User user) {
        return new AsePrincipal(user, null);
    }

    public static AsePrincipal newOAuth2User(User user, Map<String, Object> attributes) {
        return new AsePrincipal(user, attributes);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return user
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
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getLocked();
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
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
