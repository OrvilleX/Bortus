package com.orvillex.bortus.modules.system.repository;

import com.orvillex.bortus.modules.system.domain.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

/**
 * 字段仓储
 * @author y-z-f
 * @version 0.1
 */
public interface DictRepository extends JpaRepository<Dict, Long>, JpaSpecificationExecutor<Dict> {

    /**
     * 删除
     */
    void deleteByIdIn(Set<Long> ids);

    /**
     * 查询
     */
    List<Dict> findByIdIn(Set<Long> ids);
}
