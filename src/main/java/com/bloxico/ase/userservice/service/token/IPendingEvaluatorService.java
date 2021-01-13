package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.web.model.token.IPendingEvaluatorRequest;

import java.util.List;

public interface IPendingEvaluatorService {

    PendingEvaluatorDto createPendingEvaluator(IPendingEvaluatorRequest request, long principalId);

    String getPendingEvaluatorToken(String email);

    void checkInvitationToken(String token);

    void deletePendingEvaluator(String email);

    void consumePendingEvaluator(String email, String token);

    List<PendingEvaluatorDto> searchPendingEvaluators(String email, int page, int size, String sort);

}
