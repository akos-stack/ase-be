package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import com.bloxico.ase.userservice.repository.token.BlacklistedJwtRepository;
import com.bloxico.ase.userservice.util.JwtBlacklistInMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class StartupConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private BlacklistedJwtRepository blacklistedJwtRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent __) {
        log.info("StartupConfig.onApplicationEvent - start");
        initJwtBlacklistInMemory();
        log.info("StartupConfig.onApplicationEvent - end");
    }

    private void initJwtBlacklistInMemory() {
        log.info("StartupConfig.initJwtBlacklistInMemory - start");
        JwtBlacklistInMemory
                .addAll(blacklistedJwtRepository
                        .findAll()
                        .stream()
                        .map(BlacklistedJwt::getToken)
                        .collect(toList()));
        log.info("StartupConfig.initJwtBlacklistInMemory - end");
    }

}
