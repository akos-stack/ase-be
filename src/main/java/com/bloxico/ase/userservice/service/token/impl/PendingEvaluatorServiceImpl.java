package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDocumentDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.entity.token.*;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorDocumentRepository;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.service.token.IPendingEvaluatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_NOT_FOUND;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.RESUME_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class PendingEvaluatorServiceImpl implements IPendingEvaluatorService {

    private final PendingEvaluatorRepository pendingEvaluatorRepository;
    private final PendingEvaluatorDocumentRepository pendingEvaluatorDocumentRepository;

    @Autowired
    public PendingEvaluatorServiceImpl(PendingEvaluatorRepository pendingEvaluatorRepository, PendingEvaluatorDocumentRepository pendingEvaluatorDocumentRepository) {
        this.pendingEvaluatorRepository = pendingEvaluatorRepository;
        this.pendingEvaluatorDocumentRepository = pendingEvaluatorDocumentRepository;
    }

    @Override
    public PendingEvaluatorDto createPendingEvaluator(PendingEvaluatorDto request, long principalId) {
        log.debug("PendingEvaluatorServiceImpl.createPendingEvaluator - start | request: {}, principalId: {}", request, principalId);
        requireNonNull(request);
        var status = request.getStatus();
        var pendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(request.getEmail())
                .map(status::requireDifferentStatus)
                .map(evaluator -> updateStatus(evaluator, status, principalId))
                .orElseGet(() -> newPendingEvaluator(request, principalId));
        var pendingEvaluatorDto = MAPPER.toDto(pendingEvaluator);
        log.debug("PendingEvaluatorServiceImpl.createPendingEvaluator - end | request: {}, principalId: {}", request, principalId);
        return pendingEvaluatorDto;
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
    public void checkInvitationToken(String token) {
        log.debug("PendingEvaluatorServiceImpl.checkInvitationToken - start | token: {}", token);
        requireNonNull(token);
        pendingEvaluatorRepository
                .findByToken(token)
                .map(PendingEvaluator::getStatus)
                .filter(INVITED::equals)
                .orElseThrow(TOKEN_NOT_FOUND::newException);
        log.debug("PendingEvaluatorServiceImpl.checkInvitationToken - end | token: {}", token);
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
    public Page<PendingEvaluatorDto> searchPendingEvaluators(String email, int page, int size, String sort) {
        log.debug("PendingEvaluatorServiceImpl.searchPendingEvaluators - start | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        requireNonNull(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var pendingEvaluatorsDto = pendingEvaluatorRepository
                .findAllByEmailContaining(email, pageable)
                .map(MAPPER::toDto);
        log.debug("PendingEvaluatorServiceImpl.searchPendingEvaluators - end | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        return pendingEvaluatorsDto;
    }

    @Override
    public PendingEvaluatorDocumentDto getEvaluatorResume(String email, long principalId) {
        log.debug("PendingEvaluatorServiceImpl.getEvaluatorResume - start | email: {}, principalId {}", email, principalId);
        requireNonNull(email);
        var pendingEvaluatorDocument = pendingEvaluatorDocumentRepository
                .findByPendingEvaluatorDocumentId_Email(email).orElseThrow(RESUME_NOT_FOUND::newException);
        log.debug("PendingEvaluatorServiceImpl.getEvaluatorResume - end | email: {}, principalId: {}", email, principalId);
        return MAPPER.toDto(pendingEvaluatorDocument);
    }

    @Override
    public void savePendingEvaluatorDocument(String email, long documentId) {
        log.debug("PendingEvaluatorServiceImpl.savePendingEvaluatorDocument - start | email: {}, documentId {}", email, documentId);
        requireNonNull(email);
        var pendingEvaluatorDocument = new PendingEvaluatorDocument();
        var pendingEvaluatorDocumentId = new PendingEvaluatorDocumentId();
        pendingEvaluatorDocumentId.setDocumentId(documentId);
        pendingEvaluatorDocumentId.setEmail(email);
        pendingEvaluatorDocument.setPendingEvaluatorDocumentId(pendingEvaluatorDocumentId);
        pendingEvaluatorDocumentRepository.saveAndFlush(pendingEvaluatorDocument);
        log.debug("PendingEvaluatorServiceImpl.savePendingEvaluatorDocument - end | email: {}, documentId {}", email, documentId);
    }


    private PendingEvaluator updateStatus(PendingEvaluator pendingEvaluator, Status status, long principalId) {
        pendingEvaluator.setStatus(status);
        pendingEvaluator.setUpdaterId(principalId);
        return pendingEvaluatorRepository.saveAndFlush(pendingEvaluator);
    }

    private PendingEvaluator newPendingEvaluator(PendingEvaluatorDto request, long principalId) {
        var pendingEvaluator = MAPPER.toEntity(request);
        pendingEvaluator.setToken(UUID.randomUUID().toString());
        pendingEvaluator.setCreatorId(principalId);
        return pendingEvaluatorRepository.saveAndFlush(pendingEvaluator);
    }

}
