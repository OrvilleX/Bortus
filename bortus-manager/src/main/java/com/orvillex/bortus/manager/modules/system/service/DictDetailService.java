package com.orvillex.bortus.manager.modules.system.service;

import com.orvillex.bortus.manager.modules.system.service.dto.DictDetailDto;
import com.orvillex.bortus.manager.modules.system.domain.DictDetail;
import com.orvillex.bortus.manager.modules.system.service.dto.DictDetailQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 字典明细服务
 * @author y-z-f
 * @version 0.1
 */
public interface DictDetailService {

    /**
     * 创建
     */
    void create(DictDetail resources);

    /**
     * 编辑
     */
    void update(DictDetail resources);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 分页查询
     */
    Map<String,Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable);

    /**
     * 根据字典名称获取字典详情
     */
    List<DictDetailDto> getDictByName(String name);
}
