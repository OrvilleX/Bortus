package com.orvillex.bortus.modules.scheduler.repository;

import com.orvillex.bortus.modules.scheduler.domain.QuartzJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 调度任务仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface QuartzJobRepository extends JpaRepository<QuartzJob, Long>, JpaSpecificationExecutor<QuartzJob> {

    /**
     * 查询启用的任务
     */
    List<QuartzJob> findByIsPauseIsFalse();
}
