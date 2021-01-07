package com.orvillex.bortus.manager.modules.scheduler.repository;

import java.util.Date;
import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobRegistry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 执行器仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface JobRegistryRepository extends JpaRepository<JobRegistry, Long>, JpaSpecificationExecutor<JobRegistry> {
    
    @Query(value = "SELECT m.registry_id FROM sys_job_registry AS m WHERE m.update_time < DATE_ADD(?2, INTERVAL - ?1 SECOND)", nativeQuery = true)
    List<Long> findDead(Integer timeout, Date nowTime);

    @Query(value = "SELECT m.* FROM sys_job_registry AS m WHERE m.update_time < DATE_ADD(?2, INTERVAL - ?1 SECOND)", nativeQuery = true)
    List<JobRegistry> findAll(Integer timeout, Date nowTime);

    @Modifying
    @Query(value = "DELETE FROM sys_job_registry WHERE registry_group = ?1 AND registry_key = ?2 AND registry_value = ?3", nativeQuery = true)
    void delete(String registryGroup, String registryKey, String registryValue);
}
