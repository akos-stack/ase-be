package com.bloxico.userservice.util.tokencreator;

import com.bloxico.userservice.entities.token.Token;
import com.bloxico.userservice.repository.token.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
import java.util.Random;

@Slf4j
public class TokenCreatorUtil<T extends Token> {

    private TokenRepository<T> tokenRepository;

    @Value("${token.digit.size}")
    private int tokenDigitSize;

    public TokenCreatorUtil(TokenRepository<T> tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String createTokenCode() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        boolean unique = false;

        while (!unique) {
            stringBuilder.setLength(0);
            for (int count = 0; count < tokenDigitSize; count++) {
                stringBuilder.append(random.nextInt(10));
            }

            unique = codeIsUnique(stringBuilder.toString());
        }

        log.debug("Token value generated: " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private boolean codeIsUnique(String code) {
        Optional<T> op = tokenRepository.findByTokenValue(code);

        return !op.isPresent();
    }
}
