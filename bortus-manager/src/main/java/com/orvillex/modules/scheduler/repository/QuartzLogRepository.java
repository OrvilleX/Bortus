package com.orvillex.modules.scheduler.repository;

import com.orvillex.modules.scheduler.domain.QuartzLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 调度日志接口
 * @author y-z-f
 * @version 0.1
 */
public interface QuartzLogRepository extends JpaRepository<QuartzLog,Long>, JpaSpecificationExecutor<QuartzLog> {

}
