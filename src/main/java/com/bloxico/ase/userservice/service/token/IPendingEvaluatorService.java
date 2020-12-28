package com.bloxico.ase.userservice.service.token;

public interface IPendingEvaluatorService {

    String createPendingEvaluator(String email, String cvPath, boolean invited, long principalId);

    String getPendingEvaluatorToken(String email);

    void deletePendingEvaluator(String email);

    void consumePendingEvaluator(String email, String token);

}
