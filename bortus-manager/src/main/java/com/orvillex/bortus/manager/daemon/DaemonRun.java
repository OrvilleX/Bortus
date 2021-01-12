package com.orvillex.bortus.manager.daemon;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.orvillex.bortus.manager.config.scheduler.SchedulerProperties;
import com.orvillex.bortus.manager.config.thread.AsyncTaskProperties;
import com.orvillex.bortus.manager.utils.JobSchedulerUtils;
import com.orvillex.bortus.manager.utils.ThreadPoolExecutorUtil;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DaemonRun implements InitializingBean, DisposableBean {
    private ThreadPoolExecutor pool;

    @Resource
    private SchedulerProperties schedulerProperties;

    @Resource
    private AsyncTaskProperties asyncTaskProperties;

    @Override
    public void destroy() throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = ThreadPoolExecutorUtil.getPoll(asyncTaskProperties);
        JobSchedulerUtils.ACCESSTOKEN = schedulerProperties.getAccessToken();
    }
}
