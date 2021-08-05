package com.orvillex.bortus.manager.repository;

import com.orvillex.bortus.manager.entity.BaseEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity,ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    
    /**
     * 逻辑软删除
     * @param id
     */
    @Query("update #{#entityName} e set e.deleted = 1 where e.id = ?1")
    @Transactional
    @Modifying
    void logicDelete(ID id);
}
