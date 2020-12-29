package com.orvillex.bortus.manager.modules.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.orvillex.bortus.manager.enums.DataScopeType;
import com.orvillex.bortus.manager.modules.system.domain.User;
import com.orvillex.bortus.manager.modules.system.repository.RoleRepository;
import com.orvillex.bortus.manager.modules.system.service.automap.DeptMapper;
import com.orvillex.bortus.manager.modules.system.service.dto.DeptDto;
import com.orvillex.bortus.manager.modules.system.service.dto.DeptQueryCriteria;
import com.orvillex.bortus.manager.utils.*;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.system.domain.Dept;
import com.orvillex.bortus.manager.modules.system.repository.DeptRepository;
import com.orvillex.bortus.manager.modules.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 * @author y-z-f
 * @version 0.1
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dept")
public class DeptServiceImpl implements DeptService {
    private final DeptRepository deptRepository;
    private final DeptMapper deptMapper;
    private final UserRepository userRepository;
    private final RedisUtils redisUtils;
    private final RoleRepository roleRepository;

    @Override
    public List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws Exception {
        Sort sort = new Sort(Sort.Direction.ASC, "deptSort");
        String dataScopeType = SecurityUtils.getDataScopeType();
        if (isQuery) {
            if (dataScopeType.equals(DataScopeType.ALL.getValue())) {
                criteria.setPidIsNull(true);
            }
            List<Field> fields = QueryHelp.getAllFields(criteria.getClass(), new ArrayList<>());
            List<String> fieldNames = new ArrayList<String>(){{add("pidIsNull");add("enabled");}};
            for (Field field : fields) {
                field.setAccessible(true);
                Object val = field.get(criteria);
                if (fieldNames.contains(field.getName())) {
                    continue;
                }
                if (ObjectUtil.isNotNull(val)) {
                    criteria.setPidIsNull(null);
                    break;
                }
            }
        }
        List<DeptDto> list = deptMapper.toDto(deptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), sort));
        if (StringUtils.isBlank(dataScopeType)) {
            return deduplication(list);
        }
        return list;
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    public DeptDto findById(Long id) {
        Dept dept = deptRepository.findById(id).orElseGet(Dept::new);
        ValidationUtil.isNull(dept.getId(), "Dept", "id", id);
        return deptMapper.toDto(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Dept resources) {
        deptRepository.save(resources);
        resources.setSubCount(0);
        updateSubCnt(resources.getPid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Dept resources) {
        Long oldPid = findById(resources.getId()).getPid();
        Long newPid = resources.getPid();
        if (resources.getPid() != null && resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级节点不能为自身");
        }
        Dept dept = deptRepository.findById(resources.getId()).orElseGet(Dept::new);
        ValidationUtil.isNull(dept.getId(), "Dept", "id", resources.getId());
        resources.setId(dept.getId());
        deptRepository.save(resources);
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
        delCaches(resources.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<DeptDto> deptDtos) {
        for (DeptDto deptDto : deptDtos) {
            delCaches(deptDto.getId());
            deptRepository.deleteById(deptDto.getId());
            updateSubCnt(deptDto.getPid());
        }
    }

    @Override
    public List<Dept> findByPid(Long pid) {
        return deptRepository.findByPid(pid);
    }

    @Override
    public Set<Dept> findByRoleId(Long id) {
        return deptRepository.findByRoleId(id);
    }

    @Override
    public void download(List<DeptDto> deptDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeptDto deptDto : deptDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部门名称", deptDto.getName());
            map.put("部门状态", deptDto.getEnabled() ? "启用" : "停用");
            map.put("创建日期", deptDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Set<DeptDto> getDeleteDepts(List<Dept> deptList, Set<DeptDto> deptDtos) {
        for (Dept dept : deptList) {
            deptDtos.add(deptMapper.toDto(dept));
            List<Dept> depts = deptRepository.findByPid(dept.getId());
            if (depts != null && depts.size() != 0) {
                getDeleteDepts(depts, deptDtos);
            }
        }
        return deptDtos;
    }

    @Override
    public List<DeptDto> getSuperior(DeptDto deptDto, List<Dept> depts) {
        if (deptDto.getPid() == null) {
            depts.addAll(deptRepository.findByPidIsNull());
            return deptMapper.toDto(depts);
        }
        depts.addAll(deptRepository.findByPid(deptDto.getPid()));
        return getSuperior(findById(deptDto.getPid()), depts);
    }

    @Override
    public Object buildTree(List<DeptDto> deptDtos) {
        Set<DeptDto> trees = new LinkedHashSet<>();
        Set<DeptDto> depts = new LinkedHashSet<>();
        List<String> deptNames = deptDtos.stream().map(DeptDto::getName).collect(Collectors.toList());
        boolean isChild;
        for (DeptDto deptDto : deptDtos) {
            isChild = false;
            if (deptDto.getPid() == null) {
                trees.add(deptDto);
            }
            for (DeptDto it : deptDtos) {
                if (it.getPid() != null && deptDto.getId().equals(it.getPid())) {
                    isChild = true;
                    if (deptDto.getChildren() == null) {
                        deptDto.setChildren(new ArrayList<>());
                    }
                    deptDto.getChildren().add(it);
                }
            }
            if (isChild) {
                depts.add(deptDto);
            } else if (deptDto.getPid() != null && !deptNames.contains(findById(deptDto.getPid()).getName())) {
                depts.add(deptDto);
            }
        }

        if (CollectionUtil.isEmpty(trees)) {
            trees = depts;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("totalElements", deptDtos.size());
        map.put("content", trees);
        return map;
    }

    @Override
    public List<Long> getDeptChildren(Long deptId, List<Dept> deptList) {
        List<Long> list = new ArrayList<>();
        deptList.forEach(dept -> {
            if (dept != null && dept.getEnabled()) {
                List<Dept> depts = deptRepository.findByPid(dept.getId());
                if (deptList.size() != 0) {
                    list.addAll(getDeptChildren(dept.getId(), depts));
                }
                list.add(dept.getId());
            }
        });
        return list;
    }

    @Override
    public void verification(Set<DeptDto> deptDtos) {
        Set<Long> deptIds = deptDtos.stream().map(DeptDto::getId).collect(Collectors.toSet());
        if (userRepository.countByDepts(deptIds) > 0) {
            throw new BadRequestException("部门下存在用户");
        }
        if (roleRepository.countByDepts(deptIds) > 0) {
            throw new BadRequestException("部门下存在角色");
        }
    }

    /**
     * 更新子部门数量
     */
    private void updateSubCnt(Long deptId) {
        if (deptId != null) {
            int count = deptRepository.countByPid(deptId);
            deptRepository.updateSubCntById(count, deptId);
        }
    }

    /**
     * 去重
     */
    private List<DeptDto> deduplication(List<DeptDto> list) {
        List<DeptDto> deptDtos = new ArrayList<>();
        for (DeptDto deptDto : list) {
            boolean flag = true;
            for (DeptDto dto : list) {
                if (dto.getId().equals(deptDto.getPid())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                deptDtos.add(deptDto);
            }
        }
        return deptDtos;
    }

    /**
     * 清除缓存
     */
    public void delCaches(Long id) {
        List<User> users = userRepository.findByDeptRoleId(id);
        redisUtils.delByKeys("data::user:", users.stream().map(User::getId).collect(Collectors.toSet()));
        redisUtils.delete("dept::id:" + id);
    }
}
