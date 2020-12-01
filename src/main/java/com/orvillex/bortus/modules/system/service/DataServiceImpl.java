package com.orvillex.bortus.modules.system.service;

import com.orvillex.bortus.enums.DataScopeType;
import com.orvillex.bortus.modules.system.domain.Dept;
import com.orvillex.bortus.modules.system.service.dto.RoleSmallDto;
import com.orvillex.bortus.modules.system.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据权限实现
 * @author y-z-f
 * @version 0.1
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "data")
public class DataServiceImpl  implements DataService {
    private final RoleService roleService;
    private final DeptService deptService;

    @Override
    @Cacheable(key = "'user': + #p0.id")
    public List<Long> getDeptIds(UserDto user) {
        Set<Long> deptIds = new HashSet<>();
        List<RoleSmallDto> roleSet = roleService.findByUsersId(user.getId());
        for (RoleSmallDto role : roleSet) {
            DataScopeType dataScopeType = DataScopeType.find(role.getDataScope());
            switch (Objects.requireNonNull(dataScopeType)) {
                case THIS_LEVEL:
                    deptIds.add(user.getDept().getId());
                    break;
                case CUSTOMIZE:
                    deptIds.addAll(getCustomize(deptIds, role));
                    break;
                default:
                    return new ArrayList<>(deptIds);
            }
        }
        return new ArrayList<>(deptIds);
    }

    /**
     * 获取自定义数据权限
     */
    public Set<Long> getCustomize(Set<Long> deptIds, RoleSmallDto role) {
        Set<Dept> depts = deptService.findByRoleId(role.getId());
        for (Dept dept : depts) {
            deptIds.add(dept.getId());
            List<Dept> deptChildren = deptService.findByPid(dept.getId());
            if (deptChildren != null && deptChildren.size() != 0) {
                deptIds.addAll(deptService.getDeptChildren(dept.getId(), deptChildren));
            }
        }
        return deptIds;
    }
}
