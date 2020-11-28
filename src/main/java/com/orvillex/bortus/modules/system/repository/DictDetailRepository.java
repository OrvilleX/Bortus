package com.orvillex.bortus.modules.system.repository;

import com.orvillex.bortus.modules.system.domain.DictDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 字典详情仓库接口
 * @author y-z-f
 * @version 0.1
 */
public interface DictDetailRepository extends JpaRepository<DictDetail, Long>, JpaSpecificationExecutor<DictDetail> {

    /**
     * 根据名称查询
     */
    List<DictDetail> findByDictName(String name);
}
