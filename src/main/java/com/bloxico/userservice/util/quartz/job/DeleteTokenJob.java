package com.bloxico.userservice.util.quartz.job;

import com.bloxico.userservice.facade.IDeleteTokenQuartzFacade;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class DeleteTokenJob extends QuartzJobBean {

    @Autowired
    private IDeleteTokenQuartzFacade deleteTokenFacade;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.debug("Executing job!");

        deleteTokenFacade.performTokenDelete();

        log.debug("Execution completed!");
    }
}