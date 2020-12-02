package com.orvillex.bortus.config.scheduler;

import com.orvillex.bortus.modules.scheduler.domain.QuartzJob;
import com.orvillex.bortus.modules.scheduler.repository.QuartzJobRepository;
import com.orvillex.bortus.utils.scheduler.QuartzManage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于应用启动后重新激活调度任务
 * @author y-z-f
 * @version 0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {
    private final QuartzJobRepository quartzJobRepository;
    private final QuartzManage quartzManage;

    @Override
    public void run(ApplicationArguments applicationArguments) {
        log.info("--------------------注入定时任务---------------------");
        List<QuartzJob> quartzJobs = quartzJobRepository.findByIsPauseIsFalse();
        quartzJobs.forEach(quartzManage::addJob);
        log.info("--------------------定时任务注入完成---------------------");
    }
}
