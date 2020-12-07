package com.bloxico.userservice.facade.impl;

import com.bloxico.userservice.facade.IDeleteTokenQuartzFacade;
import com.bloxico.userservice.services.token.impl.PasswordTokenServiceImpl;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImpl;
import com.bloxico.ase.userservice.service.user.IUserRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
public class DeleteTokenQuartzFacadeImpl implements IDeleteTokenQuartzFacade {

    private VerificationTokenServiceImpl verificationTokenService;
    private PasswordTokenServiceImpl passwordTokenService;
    private IUserRegistrationService userRegistrationService;

    @Autowired
    public DeleteTokenQuartzFacadeImpl(VerificationTokenServiceImpl verificationTokenService,
                                       PasswordTokenServiceImpl passwordTokenService,
                                       IUserRegistrationService userRegistrationService) {
        this.verificationTokenService = verificationTokenService;
        this.passwordTokenService = passwordTokenService;
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public void performTokenDelete() {
        log.debug("Perform token delete - start");
        passwordTokenService.deleteExpiredTokens();

        List<Long> userIdsAssignedToDeletedTokens = verificationTokenService.deleteExpiredTokens();

        List<Long> deletedUserIds = userRegistrationService.deleteDisabledUsersWithIds(userIdsAssignedToDeletedTokens);

        log.debug("Perform token delete - end");
    }
}
