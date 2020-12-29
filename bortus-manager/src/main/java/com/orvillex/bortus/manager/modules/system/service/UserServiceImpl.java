package com.orvillex.bortus.manager.modules.system.service;

import com.orvillex.bortus.manager.exception.EntityNotFoundException;
import com.orvillex.bortus.manager.modules.security.service.UserCacheClean;
import com.orvillex.bortus.manager.modules.system.domain.User;
import com.orvillex.bortus.manager.modules.system.service.automap.UserMapper;
import com.orvillex.bortus.manager.modules.system.service.dto.JobSmallDto;
import com.orvillex.bortus.manager.modules.system.service.dto.RoleSmallDto;
import com.orvillex.bortus.manager.utils.*;
import com.orvillex.bortus.manager.config.FileProperties;
import com.orvillex.bortus.manager.exception.EntityExistException;
import com.orvillex.bortus.manager.modules.security.service.OnlineUserService;
import com.orvillex.bortus.manager.modules.system.repository.UserRepository;
import com.orvillex.bortus.manager.modules.system.service.dto.UserDto;
import com.orvillex.bortus.manager.modules.system.service.dto.UserQueryCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 * @author y-z-f
 * @version 0.1
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileProperties properties;
    private final RedisUtils redisUtils;
    private final UserCacheClean userCacheClean;
    private final OnlineUserService onlineUserService;

    @Override
    @Cacheable(key = "'id:' + #p0")
    @Transactional(rollbackFor = Exception.class)
    public UserDto findById(long id) {
        User user = userRepository.findById(id).orElseGet(User::new);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(User resources) {
        if (userRepository.findByUsername(resources.getUsername()) != null) {
            throw new EntityExistException(User.class, "username", resources.getUsername());
        }
        if (userRepository.findByEmail(resources.getEmail()) != null) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }
        userRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User resources) {
        User user = userRepository.findById(resources.getId()).orElseGet(User::new);
        ValidationUtil.isNull(user.getId(), "User", "id", resources.getId());
        User userbyusername = userRepository.findByUsername(resources.getUsername());
        User userbyemail = userRepository.findByEmail(resources.getEmail());

        if (userbyusername != null && !user.getId().equals(userbyusername.getId())) {
            throw new EntityExistException(User.class, "username", resources.getUsername());
        }

        if (userbyemail != null && !user.getId().equals(userbyemail.getId())) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }
        if (!resources.getRoles().equals(user.getRoles())) {
            redisUtils.delete(CacheKey.DATE_USER + resources.getId());
            redisUtils.delete(CacheKey.MENU_USER + resources.getId());
            redisUtils.delete(CacheKey.ROLE_AUTH + resources.getId());
        }
        if(!resources.getUsername().equals(user.getUsername())){
            redisUtils.delete("user::username:" + user.getUsername());
        }
        if(!resources.getEnabled()){
            onlineUserService.kickOutForUsername(resources.getUsername());
        }
        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setRoles(resources.getRoles());
        user.setDept(resources.getDept());
        user.setJobs(resources.getJobs());
        user.setPhone(resources.getPhone());
        user.setNickName(resources.getNickName());
        user.setGender(resources.getGender());
        userRepository.save(user);
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            UserDto user = findById(id);
            delCaches(user.getId(), user.getUsername());
        }
        userRepository.deleteAllByIdIn(ids);
    }

    @Override
    @Cacheable(key = "'username:' + #p0")
    public UserDto findByName(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            return userMapper.toDto(user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(String username, String encryptPassword) {
        userRepository.updatePass(username, encryptPassword, new Date());
        redisUtils.delete("user::username:" + username);
        flushCache(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> updateAvatar(MultipartFile multipartFile) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername());
        String oldPath = user.getAvatarPath();
        File file = FileUtil.upload(multipartFile, properties.getPath().getAvatar());
        user.setAvatarPath(Objects.requireNonNull(file).getPath());
        user.setAvatarName(file.getName());
        userRepository.save(user);
        if (StringUtils.isNotBlank(oldPath)) {
            FileUtil.del(oldPath);
        }
        @NotBlank String username = user.getUsername();
        redisUtils.delete(CacheKey.USER_NAME + username);
        flushCache(username);
        return new HashMap<String, String>(1) {{
            put("avatar", file.getName());
        }};
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String username, String email) {
        userRepository.updateEmail(username, email);
        redisUtils.delete(CacheKey.USER_NAME + username);
        flushCache(username);
    }

    @Override
    public Object queryAll(UserQueryCriteria criteria, Pageable pageable) {
        Page<User> page = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(userMapper::toDto));
    }

    @Override
    public List<UserDto> queryAll(UserQueryCriteria criteria) {
        List<User> users = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return userMapper.toDto(users);
    }

    @Override
    public void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserDto userDTO : queryAll) {
            List<String> roles = userDTO.getRoles().stream().map(RoleSmallDto::getName).collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", userDTO.getUsername());
            map.put("角色", roles);
            map.put("部门", userDTO.getDept().getName());
            map.put("岗位", userDTO.getJobs().stream().map(JobSmallDto::getName).collect(Collectors.toList()));
            map.put("邮箱", userDTO.getEmail());
            map.put("状态", userDTO.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", userDTO.getPhone());
            map.put("修改密码的时间", userDTO.getPwdResetTime());
            map.put("创建日期", userDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCenter(User resources) {
        User user = userRepository.findById(resources.getId()).orElseGet(User::new);
        user.setNickName(resources.getNickName());
        user.setPhone(resources.getPhone());
        user.setGender(resources.getGender());
        userRepository.save(user);
        delCaches(user.getId(), user.getUsername());
    }

    /**
     * 清除缓存
     */
    public void delCaches(Long id, String username) {
        redisUtils.delete(CacheKey.USER_ID + id);
        redisUtils.delete(CacheKey.USER_NAME + username);
        flushCache(username);
    }

    /**
     * 清除已登录用户缓存
     */
    private void flushCache(String username) {
        userCacheClean.cleanUserCache(username);
    }
}
