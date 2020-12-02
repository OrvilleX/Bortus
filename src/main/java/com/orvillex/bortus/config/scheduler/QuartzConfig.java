package com.orvillex.bortus.config.scheduler;

import org.quartz.Scheduler;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * 调度任务注入配置
 * @author y-z-f
 * @version 0.1
 */
@Configuration
public class QuartzConfig {
    /**
     * 解决Job中注入Spring Bean为null的问题
     */
    @Component("quartzJobFactory")
    public static class QuartzJobFactory extends AdaptableJobFactory {

        private final AutowireCapableBeanFactory capableBeanFactory;

        public QuartzJobFactory(AutowireCapableBeanFactory capableBeanFactory) {
            this.capableBeanFactory = capableBeanFactory;
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object jobInstance = super.createJobInstance(bundle);
            capableBeanFactory.autowireBean(jobInstance);
            return jobInstance;
        }
    }

    /**
     * 注入scheduler到spring
     */
    @Bean(name = "scheduler")
    public Scheduler scheduler(QuartzJobFactory quartzJobFactory) throws Exception {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(quartzJobFactory);
        factoryBean.afterPropertiesSet();
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }
}
