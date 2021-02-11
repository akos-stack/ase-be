package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.service.token.IPendingEvaluatorService;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.token.IPendingEvaluatorRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status.INVITED;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Token.TOKEN_NOT_FOUND;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.RESUME_NOT_FOUND;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@Service
public class PendingEvaluatorServiceImpl implements IPendingEvaluatorService {

    private final PendingEvaluatorRepository pendingEvaluatorRepository;

    private final IS3Service s3Service;

    @Autowired
    public PendingEvaluatorServiceImpl(PendingEvaluatorRepository pendingEvaluatorRepository, IS3Service s3Service) {
        this.pendingEvaluatorRepository = pendingEvaluatorRepository;
        this.s3Service = s3Service;
    }

    @Override
    public PendingEvaluatorDto createPendingEvaluator(IPendingEvaluatorRequest request, long principalId) {
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var pendingEvaluatorsDto = pendingEvaluatorRepository
                .findAllByEmailContaining(email, pageable)
                .map(MAPPER::toDto);
        log.debug("PendingEvaluatorServiceImpl.searchPendingEvaluators - end | email: {}, page: {}, size: {}, sort {}", email, page, size, sort);
        return pendingEvaluatorsDto;
    }

    @Override
    public ByteArrayResource getEvaluatorResume(String email, long principalId) {
        log.debug("PendingEvaluatorServiceImpl.getEvaluatorResume - start | email: {}, principalId {}", email, principalId);
        requireNonNull(email);
        var cvPath = pendingEvaluatorRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(TOKEN_NOT_FOUND::newException)
                .getCvPath();
        if (isEmpty(cvPath))
            throw RESUME_NOT_FOUND.newException();
        var resume = s3Service.downloadFile(cvPath);
        log.debug("PendingEvaluatorServiceImpl.getEvaluatorResume - end | email: {}, principalId {}", email, principalId);
        return resume;
    }

    private PendingEvaluator updateStatus(PendingEvaluator pendingEvaluator, Status status, long principalId) {
        pendingEvaluator.setStatus(status);
        pendingEvaluator.setUpdaterId(principalId);
        return pendingEvaluatorRepository.saveAndFlush(pendingEvaluator);
    }

    private PendingEvaluator newPendingEvaluator(IPendingEvaluatorRequest request, long principalId) {
        var pendingEvaluator = MAPPER.toPendingEvaluator(request);
        pendingEvaluator.setToken(UUID.randomUUID().toString());
        pendingEvaluator.setCreatorId(principalId);
        if (request.getCv() != null) {
            var filePath = s3Service.uploadFile(FileCategory.CV, request.getCv());
            pendingEvaluator.setCvPath(filePath);
        }
        return pendingEvaluatorRepository.saveAndFlush(pendingEvaluator);
    }

}
