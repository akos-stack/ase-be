package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.service.token.IPendingEvaluatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class PendingEvaluatorServiceImpl implements IPendingEvaluatorService {

    private final PendingEvaluatorRepository pendingEvaluatorRepository;

    @Autowired
    public PendingEvaluatorServiceImpl(PendingEvaluatorRepository pendingEvaluatorRepository) {
        this.pendingEvaluatorRepository = pendingEvaluatorRepository;
    }

    @Override
    public String createPendingEvaluator(String email) {
        log.debug("PendingEvaluatorServiceImpl.createPendingEvaluator - start | email: {}", email);
        requireNonNull(email);
        var token = UUID.randomUUID().toString();
        var pendingEvaluator = new PendingEvaluator();
        pendingEvaluator.setEmail(email);
        pendingEvaluator.setToken(token);
        pendingEvaluatorRepository.save(pendingEvaluator);
        log.debug("PendingEvaluatorServiceImpl.createPendingEvaluator - end | email: {}", email);
        return token;
    }

    @Override
    public void deletePendingEvaluator(String email) {
        log.debug("PendingEvaluatorServiceImpl.deletePendingEvaluator - start | email: {}", email);
        requireNonNull(email);
        pendingEvaluatorRepository.deleteById(email);
        log.debug("PendingEvaluatorServiceImpl.deletePendingEvaluator - end | email: {}", email);
    }

    @Override
    public void consumePendingEvaluator(String email, String token) {
        log.debug("PendingEvaluatorServiceImpl.consumePendingEvaluator - start | email: {}, token: {}", email, token);
        requireNonNull(email);
        requireNonNull(token);
        var pendingEvaluator = pendingEvaluatorRepository
                .findById(email)
                .filter(e -> e.getToken().equals(token))
                .orElseThrow(TOKEN_NOT_FOUND::newException);
        pendingEvaluatorRepository.delete(pendingEvaluator);
        log.debug("PendingEvaluatorServiceImpl.consumePendingEvaluator - end | email: {}, token: {}", email, token);
    }

}
