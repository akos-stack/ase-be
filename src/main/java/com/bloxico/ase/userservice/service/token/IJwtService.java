package com.bloxico.ase.userservice.service.token;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.dto.token.DecodedJwtDto;

public interface IJwtService {

    String generateToken(UserProfileDto userProfile);

    DecodedJwtDto verifyToken(String token);

    void blacklistToken(long principalId, String token);

}
