package com.orvillex.bortus.modules.system.service;

import com.orvillex.bortus.modules.system.domain.Dict;
import com.orvillex.bortus.modules.system.domain.DictDetail;
import com.orvillex.bortus.modules.system.repository.DictDetailRepository;
import com.orvillex.bortus.modules.system.repository.DictRepository;
import com.orvillex.bortus.modules.system.service.automap.DictDetailMapper;
import com.orvillex.bortus.modules.system.service.dto.DictDetailDto;
import com.orvillex.bortus.modules.system.service.dto.DictDetailQueryCriteria;
import com.orvillex.bortus.utils.PageUtil;
import com.orvillex.bortus.utils.QueryHelp;
import com.orvillex.bortus.utils.RedisUtils;
import com.orvillex.bortus.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 字典明细服务实现
 * @author y-z-f
 * @version 0.1
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dict")
public class DictDetailServiceImpl implements DictDetailService {

    private final DictRepository dictRepository;
    private final DictDetailRepository dictDetailRepository;
    private final DictDetailMapper dictDetailMapper;
    private final RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DictDetail resources) {
        dictDetailRepository.save(resources);
        delCaches(resources);
    }

    @Override
    public void update(DictDetail resources) {
        DictDetail dictDetail = dictDetailRepository.findById(resources.getId()).orElseGet(DictDetail::new);
        ValidationUtil.isNull(dictDetail.getId(), "DictDetail", "id", resources.getId());
        resources.setId(dictDetail.getId());
        dictDetailRepository.save(resources);
        delCaches(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DictDetail dictDetail = dictDetailRepository.findById(id).orElseGet(DictDetail::new);
        delCaches(dictDetail);
        dictDetailRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable) {
        Page<DictDetail> page = dictDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dictDetailMapper::toDto));
    }

    @Override
    @Cacheable(key = "'name:' + #p0")
    public List<DictDetailDto> getDictByName(String name) {
        return dictDetailMapper.toDto(dictDetailRepository.findByDictName(name));
    }

    public void delCaches(DictDetail dictDetail) {
        Dict dict = dictRepository.findById(dictDetail.getDict().getId()).orElseGet(Dict::new);
        redisUtils.delete("dict:name:" + dict.getName());
    }
}
