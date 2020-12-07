package com.bloxico.userservice.config.oauth2;

import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.repository.user.CoinUserRepository;
import com.bloxico.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// DEPRECATED
//@Service
@Slf4j
public class CoinUserDetailsService implements UserDetailsService {

    private CoinUserRepository coinUserRepository;

    @Autowired
    public CoinUserDetailsService(CoinUserRepository coinUserRepository) {
        this.coinUserRepository = coinUserRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading User by username - start for username: {}", username);

        Optional<CoinUser> op = coinUserRepository.findUserWithRolesByEmailIgnoreCase(username);

        if (!op.isPresent()) {
            log.warn("Invalid username: " + username);
            throw new CoinUserException(ErrorCodes.INVALID_USERNAME.getCode());
        }

        CoinUser coinUser = op.get();
        log.debug("Loading User by username - end, Coin User: {}", coinUser);
        return new CoinUserDetails(coinUser);
    }
}
