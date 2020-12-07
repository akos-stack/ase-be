package com.bloxico.userservice.config.oauth2;

import com.bloxico.userservice.entities.user.CoinUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

// DEPRECATED
@Data
public class CoinUserDetails implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";

    private CoinUser coinUser;

    public CoinUserDetails(CoinUser coinUser) {
        this.coinUser = coinUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return coinUser.getCoinUserRoles()
                .stream()
                .map(x -> new SimpleGrantedAuthority(ROLE_PREFIX + x.getCoinRole().getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return coinUser.getPassword();
    }

    @Override
    public String getUsername() {
        return coinUser.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !coinUser.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return coinUser.isEnabled();
    }
}
