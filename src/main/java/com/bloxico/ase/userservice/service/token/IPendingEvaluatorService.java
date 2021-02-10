package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDocumentDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import org.springframework.data.domain.Page;

public interface IPendingEvaluatorService {

    PendingEvaluatorDto createPendingEvaluator(PendingEvaluatorDto request, long principalId);

    String getPendingEvaluatorToken(String email);

    void checkInvitationToken(String token);

    void deletePendingEvaluator(String email);

    void consumePendingEvaluator(String email, String token);

    Page<PendingEvaluatorDto> searchPendingEvaluators(String email, int page, int size, String sort);

    PendingEvaluatorDocumentDto getEvaluatorResume(String email, long principalId);

    void savePendingEvaluatorDocument(String email, long documentId);
}
