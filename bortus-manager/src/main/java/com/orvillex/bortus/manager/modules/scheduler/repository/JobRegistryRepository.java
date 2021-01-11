package com.orvillex.bortus.manager.modules.scheduler.repository;

import java.util.Date;
import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobRegistry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    @Query(value = "DELETE FROM sys_job_registry WHERE registry_group = ?1 AND registry_key = ?2 AND registry_value = ?3", nativeQuery = true)
    void delete(String registryGroup, String registryKey, String registryValue);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sys_job_registry SET `update_time` = ?4 WHERE `registry_group` = ?1 AND `registry_key` = ?2 AND `registry_value` = ?3", nativeQuery = true)
    Integer registryUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO sys_job_registry( `registry_group` , `registry_key` , `registry_value`, `update_time`) VALUES(?1 ,?2 ,?3 , ?4)", nativeQuery = true)
    void registrySave(String registryGroup, String registryKey, String registryValue, Date updateTime);
}
