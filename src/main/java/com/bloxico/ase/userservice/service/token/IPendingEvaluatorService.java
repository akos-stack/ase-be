package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.web.model.token.IPendingEvaluatorRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;

public interface IPendingEvaluatorService {

    PendingEvaluatorDto createPendingEvaluator(IPendingEvaluatorRequest request, long principalId);

    String getPendingEvaluatorToken(String email);

    void checkInvitationToken(String token);

    void deletePendingEvaluator(String email);

    void consumePendingEvaluator(String email, String token);

    Page<PendingEvaluatorDto> searchPendingEvaluators(String email, int page, int size, String sort);

    ByteArrayResource getEvaluatorResume(String email, long principalId);

}
