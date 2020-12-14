package com.bloxico.ase.userservice.config.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Adds auto-wiring support to quartz jobs.
 *
 * @see <a href="https://gist.github.com/jelies/5085593">
 * https://gist.github.com/jelies/5085593
 * </a>
 */
public final class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanFactory;

    public void setApplicationContext(ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        var job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }

}