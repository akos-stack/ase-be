package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDocumentDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.token.SearchPendingEvaluatorsRequest;
import org.springframework.data.domain.Page;

public interface IPendingEvaluatorService {

    PendingEvaluatorDto createPendingEvaluator(PendingEvaluatorDto request);

    String getPendingEvaluatorToken(String email);

    void checkInvitationToken(String token);

    void deletePendingEvaluator(String email);

    void consumePendingEvaluator(String email, String token);

    Page<PendingEvaluatorDto> searchPendingEvaluators(SearchPendingEvaluatorsRequest request, PageRequest page);

    PendingEvaluatorDocumentDto getEvaluatorResume(String email);

    void savePendingEvaluatorDocument(String email, long documentId);

}
