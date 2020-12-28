package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;

import java.util.List;

public interface IPendingEvaluatorService {

    String createPendingEvaluator(String email, String cvPath, boolean invited, long principalId);

    String getPendingEvaluatorToken(String email);

    void deletePendingEvaluator(String email);

    void consumePendingEvaluator(String email, String token);

    List<PendingEvaluatorDto> searchPendingEvaluators(String email,int page, int size, String sort);

}
