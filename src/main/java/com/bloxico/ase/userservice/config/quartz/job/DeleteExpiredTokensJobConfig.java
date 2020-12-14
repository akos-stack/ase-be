package com.bloxico.ase.userservice.config.quartz.job;

import com.bloxico.ase.userservice.facade.IQuartzOperationsFacade;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;

import static com.bloxico.ase.userservice.config.quartz.QuartzConfig.scheduleRepeatForever;

@Configuration
public class DeleteExpiredTokensJobConfig {

    @Slf4j
    public static class DeleteExpiredTokensJob extends QuartzJobBean {

        @Autowired
        private IQuartzOperationsFacade quartzOperationsFacade;

        @Override
        protected void executeInternal(JobExecutionContext __) {
            log.debug("DeleteExpiredTokensJob.executeInternal - start");
            quartzOperationsFacade.deleteExpiredTokens();
            log.debug("DeleteExpiredTokensJob.executeInternal - end");
        }

    }

    public static final String BEAN_NAME = "deleteExpiredTokensJob";

    @Value("${quartz.delete-expired-tokens.interval}")
    private long deleteExpiredTokensInterval;

    @Value("${quartz.delete-expired-tokens.job}")
    private String deleteExpiredTokensJob;

    @Value("${quartz.delete-expired-tokens.job.id}")
    private String deleteExpiredTokensJobId;

    @Value("${quartz.delete-expired-tokens.job.desc}")
    private String deleteExpiredTokensJobDesc;

    @Value("${quartz.delete-expired-tokens.trigger}")
    private String deleteExpiredTokensTrigger;

    @Value("${quartz.delete-expired-tokens.trigger.desc}")
    private String deleteExpiredTokensTriggerDesc;

    @Bean(name = BEAN_NAME)
    public JobDetail deleteExpiredTokensJob() {
        return JobBuilder
                .newJob(DeleteExpiredTokensJob.class)
                .withIdentity(
                        deleteExpiredTokensJobId,
                        deleteExpiredTokensJob)
                .withDescription(deleteExpiredTokensJobDesc)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger deleteExpiredTokensTrigger(@Qualifier(BEAN_NAME) JobDetail jobDetail) {
        return TriggerBuilder
                .newTrigger()
                .forJob(jobDetail)
                .withIdentity(
                        jobDetail.getKey().getName(),
                        deleteExpiredTokensTrigger)
                .withDescription(deleteExpiredTokensTriggerDesc)
                .startNow()
                .withSchedule(scheduleRepeatForever(deleteExpiredTokensInterval))
                .build();
    }

}
