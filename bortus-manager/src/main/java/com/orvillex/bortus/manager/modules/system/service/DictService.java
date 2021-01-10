package com.orvillex.bortus.manager.modules.system.service;

import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.system.domain.Dict;
import com.orvillex.bortus.manager.modules.system.service.dto.DictDto;
import com.orvillex.bortus.manager.modules.system.service.dto.DictQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DictService {

    /**
     * 分页查询
     */
    BasePage<DictDto> queryAll(DictQueryCriteria criteria, Pageable pageable);

    /**
     */
    List<DictDto> queryAll(DictQueryCriteria dict);

    /**
     * 创建
     */
    void create(Dict resources);

    /**
     * 编辑
     */
    void update(Dict resources);

    /**
     * 删除
     */
    void delete(Set<Long> ids);

    /**
     * 导出数据
     */
    void download(List<DictDto> dictDtos, HttpServletResponse response) throws IOException;
}
