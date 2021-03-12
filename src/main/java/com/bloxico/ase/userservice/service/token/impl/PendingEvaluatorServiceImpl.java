package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDocumentDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.entity.token.*;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorDocumentRepository;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.service.token.IPendingEvaluatorService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.token.SearchPendingEvaluatorsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public PendingEvaluatorServiceImpl(PendingEvaluatorRepository pendingEvaluatorRepository,
                                       PendingEvaluatorDocumentRepository pendingEvaluatorDocumentRepository)
    {
        this.pendingEvaluatorRepository = pendingEvaluatorRepository;
        this.pendingEvaluatorDocumentRepository = pendingEvaluatorDocumentRepository;
    }

    @Override
    public PendingEvaluatorDto createPendingEvaluator(PendingEvaluatorDto request) {
        log.debug("PendingEvaluatorServiceImpl.createPendingEvaluator - start | request: {}", request);
        requireNonNull(request);
        var status = request.getStatus();
        var pendingEvaluator = pendingEvaluatorRepository
                .findByEmailIgnoreCase(request.getEmail())
                .map(status::requireDifferentStatus)
                .map(evaluator -> updateStatus(evaluator, status))
                .orElseGet(() -> newPendingEvaluator(request));
        var pendingEvaluatorDto = MAPPER.toDto(pendingEvaluator);
        log.debug("PendingEvaluatorServiceImpl.createPendingEvaluator - end | request: {}", request);
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
    public Page<PendingEvaluatorDto> searchPendingEvaluators(SearchPendingEvaluatorsRequest request, PageRequest page) {
        log.debug("PendingEvaluatorServiceImpl.searchPendingEvaluators - start | request: {}, page: {}", request, page);
        requireNonNull(request);
        requireNonNull(request.getEmail());
        var pendingEvaluatorsDto = pendingEvaluatorRepository
                .findAllByEmailContaining(request.getEmail(), page.toPageable())
                .map(MAPPER::toDto);
        log.debug("PendingEvaluatorServiceImpl.searchPendingEvaluators - end | request: {}, page: {}", request, page);
        return pendingEvaluatorsDto;
    }

    @Override
    public PendingEvaluatorDocumentDto getEvaluatorResume(String email) {
        log.debug("PendingEvaluatorServiceImpl.getEvaluatorResume - start | email: {}", email);
        requireNonNull(email);
        var pendingEvaluatorDocumentDto = pendingEvaluatorDocumentRepository
                .findByPendingEvaluatorDocumentId_Email(email)
                .map(MAPPER::toDto)
                .orElseThrow(RESUME_NOT_FOUND::newException);
        log.debug("PendingEvaluatorServiceImpl.getEvaluatorResume - end | email: {}", email);
        return pendingEvaluatorDocumentDto;
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

    private PendingEvaluator updateStatus(PendingEvaluator pendingEvaluator, Status status) {
        pendingEvaluator.setStatus(status);
        return pendingEvaluatorRepository.saveAndFlush(pendingEvaluator);
    }

    private PendingEvaluator newPendingEvaluator(PendingEvaluatorDto request) {
        var pendingEvaluator = MAPPER.toEntity(request);
        pendingEvaluator.setToken(UUID.randomUUID().toString());
        return pendingEvaluatorRepository.saveAndFlush(pendingEvaluator);
    }

}
