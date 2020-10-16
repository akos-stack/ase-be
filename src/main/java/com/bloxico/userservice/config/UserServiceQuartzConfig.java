package com.bloxico.userservice.config;

import com.bloxico.userservice.util.quartz.AutoWiringSpringBeanJobFactory;
import com.bloxico.userservice.util.quartz.job.DeleteTokenJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class UserServiceQuartzConfig {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    DataSource dataSource;

    @Bean(name = UserServiceQuartzConstants.QuartzJobBeanNames.DELETE_TOKEN_JOB_BEAN)
    public JobDetail deleteTokenJob() {

        return JobBuilder.newJob(DeleteTokenJob.class)
                .withIdentity(UserServiceQuartzConstants.JobIdentities.DELETE_TOKEN_JOB_IDENTITY, UserServiceQuartzConstants.Jobs.DELETE_TOKEN_JOB)
                .withDescription(UserServiceQuartzConstants.JobDescription.DELETE_TOKEN_JOB_DESCRIPTION)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger deleteTokenTrigger(@Qualifier(UserServiceQuartzConstants.QuartzJobBeanNames.DELETE_TOKEN_JOB_BEAN) JobDetail jobDetail) {

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), UserServiceQuartzConstants.Triggers.DELETE_TOKEN_TRIGGER)
                .withDescription(UserServiceQuartzConstants.TriggerDescription.DELETE_TOKEN_TRIGGER_DESCRIPTION)
                .startAt(new Date())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(UserServiceQuartzConstants.TriggerInterval.DELETE_TOKEN_INTERVAL)
                        .repeatForever())
                .build();
    }


    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        log.debug("Configuring Job factory");

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setOverwriteExistingJobs(true);
        factory.setJobFactory(springBeanJobFactory());

        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.threadPool.threadCount", "10");
        quartzProperties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        quartzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        quartzProperties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");

        String twelveHoursInMs = Integer.toString(43000000);
        quartzProperties.setProperty("org.quartz.jobStore.misfireThreshold", twelveHoursInMs);

        factory.setDataSource(dataSource);

        factory.setQuartzProperties(quartzProperties);

        return factory;
    }

    @Bean
    public Scheduler scheduler(Map<String, JobDetail> jobMap, Set<? extends Trigger> triggers) throws SchedulerException, IOException {

//        StdSchedulerFactory factory = new StdSchedulerFactory();

        log.debug("Scheduler initialization.");
        Scheduler scheduler = schedulerFactoryBean().getScheduler();
//        scheduler.setJobFactory(springBeanJobFactory());

        Map<JobDetail, Set<? extends Trigger>> triggersAndJobs = new HashMap<>();

        for (JobDetail jobDetail : jobMap.values()) {
            for (Trigger trigger : triggers) {
                if (trigger.getJobKey().equals(jobDetail.getKey())) {
                    Set<Trigger> set = new HashSet<>();
                    set.add(trigger);
                    triggersAndJobs.put(jobDetail, set);
                }
            }
//            scheduler.addJob(jobDetail, false);
        }
        log.info("Deleting existing jobs in the database");
        scheduler.deleteJobs(jobMap.values().stream().map(JobDetail::getKey).collect(Collectors.toList()));

        log.info("Scheduling jobs");
        scheduler.scheduleJobs(triggersAndJobs, false);

        log.debug("Starting Scheduler threads");
        scheduler.start();
        return scheduler;
    }

    private static class UserServiceQuartzConstants {

        private static class Jobs {
            private static final String DELETE_TOKEN_JOB = "delete_token_job";
            private static final String PERSIST_BLOCKCHAIN_TX_JOB = "blockchain_blockchain_tx_job";
        }

        private static class JobDescription {
            private static final String DELETE_TOKEN_JOB_DESCRIPTION = "This job deletes all tokens that expired.";
            private static final String PERSIST_BLOCKCHAIN_TX_JOB_DESCRIPTION = "This job persists all stored CREATED transactions before current date";
        }

        private static class JobIdentities {
            private static final String DELETE_TOKEN_JOB_IDENTITY = "delete_token_job_id";
            private static final String PERSIST_BLOCKCHAIN_TX_JOB_IDENTITY = "persist_blockchain_tx_identity";
        }

        private static class QuartzJobBeanNames {

            private static final String DELETE_TOKEN_JOB_BEAN = "deleteTokenJobBean";
            private static final String PERSIST_BLOCKCHAIN_TX_JOB_BEAN = "persistBlockchainTxJobBean";
        }

        private static class Triggers {
            private static final String DELETE_TOKEN_TRIGGER = "delete_token_trigger";
            private static final String PERSIST_BLOCKCHAIN_TX_TRIGGER = "persist_blockchain_tx_trigger";
        }

        private static class TriggerDescription {
            private static final String DELETE_TOKEN_TRIGGER_DESCRIPTION = "This trigger calls a job that deletes all expired tokens.";
            private static final String PERSIST_BLOCKCHAIN_TX_TRIGGER_DESCRIPTION = "This trigger calls a job that persists all created transactions before current date";
        }

        private static class TriggerInterval {
            private static final int DELETE_TOKEN_INTERVAL = 24;
            private static final int TEST_BLOCKCHAIN_STORING_INTERVAL_IN_MINUTES = 30;
        }

    }
}
