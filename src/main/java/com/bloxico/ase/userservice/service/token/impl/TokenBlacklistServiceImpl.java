package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class TokenBlacklistServiceImpl implements ITokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    public TokenBlacklistServiceImpl(BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    @Cacheable("blacklistedTokensCache")
    public Set<String> blacklistedTokens() {
        log.debug("TokenBlacklistServiceImpl.blacklistedTokens - start");
        var blacklist = blacklistedTokenRepository.findDistinctTokenValues();
        log.debug("TokenBlacklistServiceImpl.blacklistedTokens - end");
        return Set.copyOf(blacklist); // immutable set
    }

    @Override
    @CacheEvict(value = "blacklistedTokensCache", allEntries = true)
    public void blacklistTokens(List<OAuthAccessTokenDto> tokens, long principalId) {
        log.debug("TokenBlacklistServiceImpl.blacklistTokens - start | tokens: {}, principalId: {}", tokens, principalId);
        requireNonNull(tokens);
        var bTokens = tokens
                .stream()
                .map(MAPPER::toBlacklistedToken)
                .peek(bt -> bt.setCreatorId(principalId))
                .collect(toList());
        blacklistedTokenRepository.saveAll(bTokens);
        log.debug("TokenBlacklistServiceImpl.blacklistTokens - end | tokens: {}, principalId: {}", tokens, principalId);
    }

    @Override
    public void checkIfBlacklisted(String token) {
        log.debug("TokenBlacklistServiceImpl.checkIfBlacklisted - start | token: {}", token);
        requireNonNull(token);
        if (blacklistedTokens().contains(token))
            throw ErrorCodes.Token.INVALID_TOKEN.newException();
        log.debug("TokenBlacklistServiceImpl.checkIfBlacklisted - start | token: {}", token);
    }

    @Override
    @CacheEvict(value = "blacklistedTokensCache", allEntries = true)
    public void deleteExpiredTokens() {
        log.debug("TokenBlacklistServiceImpl.deleteExpiredTokens - start");
        blacklistedTokenRepository.deleteExpiredTokens();
        log.debug("TokenBlacklistServiceImpl.deleteExpiredTokens - end");
    }

}
