package com.orvillex.bortus.manager.config;

import com.orvillex.bortus.manager.utils.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 权限自定义鉴别
 * @author y-z-f
 * @version 0.1
 */
@Service(value = "x")
public class XPermissionConfig {
    public Boolean check(String ...permissions) {
        List<String> xPermissions = SecurityUtils.getCurrentUser().getAuthorities()
        .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return xPermissions.contains("admin") || Arrays.stream(permissions).anyMatch(xPermissions::contains);
    }
}
