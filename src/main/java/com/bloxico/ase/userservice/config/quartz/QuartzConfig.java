package com.bloxico.ase.userservice.config.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Slf4j
@Configuration
public class QuartzConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApplicationContext applicationContext;

    public static SimpleScheduleBuilder scheduleRepeatForever(long intervalInMilliseconds) {
        return SimpleScheduleBuilder
                .simpleSchedule()
                .repeatForever()
                .withIntervalInMilliseconds(intervalInMilliseconds);
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        var jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        var factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setJobFactory(springBeanJobFactory());
        factory.setDataSource(dataSource);
        factory.setConfigLocation(new ClassPathResource("quartz.properties"));
        return factory;
    }

    @Bean
    public Scheduler scheduler(Map<String, JobDetail> jobMap,
                               Set<? extends Trigger> triggers)
            throws SchedulerException
    {
        var jobDetails = jobMap.values();
        var scheduler = schedulerFactoryBean().getScheduler();
        scheduler.deleteJobs(
                jobDetails
                        .stream()
                        .map(JobDetail::getKey)
                        .collect(toList()));
        var jobsAndTriggers = pairJobsAndTriggers(jobDetails, triggers);
        scheduler.scheduleJobs(jobsAndTriggers, false);
        scheduler.start();
        log.info("QuartzConfig.scheduler - started with scheduled jobs: {}",
                jobsAndTriggers.keySet().stream()
                        .map(JobDetail::getKey)
                        .sorted()
                        .collect(toList()));
        return scheduler;
    }

    private static Map<JobDetail, Set<? extends Trigger>>
        pairJobsAndTriggers
            (Collection<? extends JobDetail> jobs,
             Collection<? extends Trigger> triggers)
    {
        var map = new HashMap<JobDetail, Set<? extends Trigger>>();
        for (JobDetail jobDetail : jobs)
            for (Trigger trigger : triggers)
                if (trigger.getJobKey().equals(jobDetail.getKey()))
                    map.put(jobDetail, Set.of(trigger));
        return map;
    }

}
