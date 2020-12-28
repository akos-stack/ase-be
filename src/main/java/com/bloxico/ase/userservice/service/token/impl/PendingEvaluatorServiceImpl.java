package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.service.token.IPendingEvaluatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_EXISTS;
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
    public String createPendingEvaluator(String email, String cvPath, boolean invited, long principalId) {
        log.debug("PendingEvaluatorServiceImpl.createPendingEvaluator - start | email: {}, cvPath: {}, invited: {}, principalId: {}", email, cvPath, invited, principalId);
        requireNonNull(email);
        if (evaluatorAlreadyPending(email)) {
            updatePendingEvaluator(email, invited, principalId);
        }
        var token = UUID.randomUUID().toString();
        var pendingEvaluator = new PendingEvaluator();
        pendingEvaluator.setEmail(email);
        pendingEvaluator.setToken(token);
        pendingEvaluator.setCvPath(cvPath);
        if(invited) {
            pendingEvaluator.setStatus(PendingEvaluator.PendingEvaluatorStatus.PENDING);
        } else {
            pendingEvaluator.setStatus(PendingEvaluator.PendingEvaluatorStatus.REQUESTED);
        }
        pendingEvaluator.setCreatorId(principalId);
        pendingEvaluatorRepository.save(pendingEvaluator);
        log.debug("PendingEvaluatorServiceImpl.createPendingEvaluator - start | email: {}, cvPath: {}, invited: {}, principalId: {}", email, cvPath, invited, principalId);
        return token;
    }

    @Override
    public String getPendingEvaluatorToken(String email) {
        log.debug("PendingEvaluatorServiceImpl.getPendingEvaluatorToken - start | email: {}", email);
        requireNonNull(email);
        var token = pendingEvaluatorRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(TOKEN_NOT_FOUND::newException)
                .getToken();
        log.debug("PendingEvaluatorServiceImpl.getPendingEvaluatorToken - end | email: {}", email);
        return token;
    }

    @Override
    public void deletePendingEvaluator(String email) {
        log.debug("PendingEvaluatorServiceImpl.deletePendingEvaluator - start | email: {}", email);
        requireNonNull(email);
        var pendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(TOKEN_NOT_FOUND::newException);
        pendingEvaluatorRepository.delete(pendingEvaluator);
        log.debug("PendingEvaluatorServiceImpl.deletePendingEvaluator - end | email: {}", email);
    }

    @Override
    public void consumePendingEvaluator(String email, String token) {
        log.debug("PendingEvaluatorServiceImpl.consumePendingEvaluator - start | email: {}, token: {}", email, token);
        requireNonNull(email);
        requireNonNull(token);
        var pendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCaseAndToken(email, token)
                .orElseThrow(TOKEN_NOT_FOUND::newException);
        pendingEvaluatorRepository.delete(pendingEvaluator);
        log.debug("PendingEvaluatorServiceImpl.consumePendingEvaluator - end | email: {}, token: {}", email, token);
    }

    @Override
    public List<PendingEvaluatorDto> searchPendingEvaluators(String email, int page, int size, String sort) {
        log.debug("PendingEvaluatorServiceImpl.searchPendingEvaluators - start | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var pendingEvaluators = pendingEvaluatorRepository.findAllByEmailContaining(email, pageable);
        var pendingEvaluatorDtos = pendingEvaluators
                .stream()
                .map(pendingEvaluator -> MAPPER.toDto(pendingEvaluator))
                .collect(Collectors.toList());
        log.debug("PendingEvaluatorServiceImpl.searchPendingEvaluators - end | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        return pendingEvaluatorDtos;
    }

    private boolean evaluatorAlreadyPending(String email) {
        return pendingEvaluatorRepository.findByEmailIgnoreCase(email).isPresent();
    }

    private String updatePendingEvaluator(String email, boolean invited, long principalId) {
        var pendingEvaluator = pendingEvaluatorRepository.findByEmailIgnoreCase(email).get();
        if(PendingEvaluator.PendingEvaluatorStatus.PENDING == pendingEvaluator.getStatus()) {
            throw TOKEN_EXISTS.newException();
        } else {
            if(!invited) {
                throw TOKEN_EXISTS.newException();
            }
            pendingEvaluator.setStatus(PendingEvaluator.PendingEvaluatorStatus.PENDING);
            pendingEvaluator.setUpdaterId(principalId);
            pendingEvaluator = pendingEvaluatorRepository.save(pendingEvaluator);
            return pendingEvaluator.getToken();
        }
    }

}
