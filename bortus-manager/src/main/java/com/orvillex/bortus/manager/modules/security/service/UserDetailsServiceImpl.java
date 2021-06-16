package com.orvillex.bortus.manager.modules.security.service;

import com.orvillex.bortus.manager.modules.security.service.dto.JwtUserDto;
import com.orvillex.bortus.manager.config.security.LoginProperties;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.exception.EntityNotFoundException;
import com.orvillex.bortus.manager.modules.system.domain.WxUser;
import com.orvillex.bortus.manager.modules.system.service.DataService;
import com.orvillex.bortus.manager.modules.system.service.RoleService;
import com.orvillex.bortus.manager.modules.system.service.UserService;
import com.orvillex.bortus.manager.modules.system.service.WxUserService;
import com.orvillex.bortus.manager.modules.system.service.dto.UserDto;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户信息服务
 */
@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * 用户信息缓存
     */
    static Map<String, JwtUserDto> userDtoCache = new ConcurrentHashMap<>();

    private final UserService userService;
    private final RoleService roleService;
    private final DataService dataService;
    private final WxUserService wxUserService;
    private final LoginProperties loginProperties;

    public void setEnableCache(boolean enableCache) {
        this.loginProperties.setCacheEnable(enableCache);
    }

    @Override
    public JwtUserDto loadUserByUsername(String username) {
        boolean searchDb = true;
        JwtUserDto jwtUserDto = null;
        if (loginProperties.isCacheEnable() && userDtoCache.containsKey(username)) {
            jwtUserDto = userDtoCache.get(username);
            searchDb = false;
        }
        if (searchDb) {
            UserDto user;
            try {
                if (username.startsWith("#wx")) {
                    username = username.substring(3);
                    WxUser wxUser = wxUserService.findBySessionKey(username);
                    UserDto userDto = new UserDto();
                    userDto.setId(wxUser.getId());
                    userDto.setUsername(wxUser.getWxOpenId());

                    List<Long> dataScopes = new ArrayList<>();
                    List<GrantedAuthority> auth = new ArrayList<>();
                    JwtUserDto result = new JwtUserDto(
                        userDto,
                        dataScopes,
                        auth
                    );
                    return result;
                } else {
                    user = userService.findByName(username);
                }
            } catch (EntityNotFoundException e) {
                // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
                throw new UsernameNotFoundException("", e);
            }
            if (user == null) {
                throw new UsernameNotFoundException("");
            } else {
                if (!user.getEnabled()) {
                    throw new BadRequestException("账号未激活！");
                }
                jwtUserDto = new JwtUserDto(
                        user,
                        dataService.getDeptIds(user),
                        roleService.mapToGrantedAuthorities(user)
                );
                userDtoCache.put(username, jwtUserDto);
            }
        }
        return jwtUserDto;
    }
}
