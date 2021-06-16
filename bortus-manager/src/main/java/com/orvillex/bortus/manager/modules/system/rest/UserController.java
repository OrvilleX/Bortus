package com.orvillex.bortus.manager.modules.system.rest;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.config.RsaProperties;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.system.domain.User;
import com.orvillex.bortus.manager.modules.system.service.*;
import com.orvillex.bortus.manager.utils.SecurityUtils;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.system.domain.vo.UserPassVo;
import com.orvillex.bortus.manager.modules.system.service.dto.RoleSmallDto;
import com.orvillex.bortus.manager.modules.system.service.dto.UserDto;
import com.orvillex.bortus.manager.modules.system.service.dto.UserQueryCriteria;
import com.orvillex.bortus.manager.utils.CacheKey;
import com.orvillex.bortus.manager.utils.PageUtil;
import com.orvillex.bortus.manager.utils.RsaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户管理API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@Api(tags = "用户管理")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final DataService dataService;
    private final DeptService deptService;
    private final RoleService roleService;
    private final VerifyService verifyService;

    @Log("导出用户数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@x.check('user:list')")
    @ApiOperation(value = "导出用户数据")
    public void download(HttpServletResponse response, UserQueryCriteria criteria) throws IOException {
        userService.download(userService.queryAll(criteria), response);
    }

    @Log("查询用户")
    @GetMapping
    @PreAuthorize("@x.check('user:list')")
    @ApiOperation(value = "查询用户")
    public ResponseEntity<BasePage<UserDto>> query(UserQueryCriteria criteria, Pageable pageable){
        if (!ObjectUtils.isEmpty(criteria.getDeptId())) {
            criteria.getDeptIds().add(criteria.getDeptId());
            criteria.getDeptIds().addAll(deptService.getDeptChildren(criteria.getDeptId(),
                    deptService.findByPid(criteria.getDeptId())));
        }
        List<Long> dataScopes = dataService.getDeptIds(userService.findByName(SecurityUtils.getCurrentUsername()));
        if (!CollectionUtils.isEmpty(criteria.getDeptIds()) && !CollectionUtils.isEmpty(dataScopes)){
            criteria.getDeptIds().retainAll(dataScopes);
            if(!CollectionUtil.isEmpty(criteria.getDeptIds())){
                return new ResponseEntity<>(userService.queryAll(criteria,pageable), HttpStatus.OK);
            }
        } else {
            criteria.getDeptIds().addAll(dataScopes);
            return new ResponseEntity<>(userService.queryAll(criteria,pageable),HttpStatus.OK);
        }
        return new ResponseEntity<>(PageUtil.toPage(null,0l),HttpStatus.OK);
    }

    @Log("新增用户")
    @PostMapping
    @PreAuthorize("@x.check('user:add')")
    @ApiOperation(value = "新增用户")
    public ResponseEntity<Object> create(@Validated @RequestBody User resources){
        checkLevel(resources);
        resources.setPassword(passwordEncoder.encode("123456"));
        userService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改用户")
    @PutMapping
    @PreAuthorize("@x.check('user:edit')")
    @ApiOperation(value = "修改用户")
    public ResponseEntity<Object> update(@Validated(User.Update.class) @RequestBody User resources){
        checkLevel(resources);
        userService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("修改用户：个人中心")
    @PutMapping(value = "center")
    @ApiOperation(value = "修改用户：个人中心")
    public ResponseEntity<Object> center(@Validated(User.Update.class) @RequestBody User resources){
        if(!resources.getId().equals(SecurityUtils.getCurrentUserId())){
            throw new BadRequestException("不能修改他人资料");
        }
        userService.updateCenter(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除用户")
    @DeleteMapping
    @PreAuthorize("@x.check('user:del')")
    @ApiOperation(value = "删除用户")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        for (Long id : ids) {
            Integer currentLevel =  Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            Integer optLevel =  Collections.min(roleService.findByUsersId(id).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BadRequestException("角色权限不足，不能删除：" + userService.findById(id).getUsername());
            }
        }
        userService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/updatePass")
    @ApiOperation(value = "修改密码")
    public ResponseEntity<Object> updatePass(@RequestBody UserPassVo passVo) throws Exception {
        String oldPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,passVo.getOldPass());
        String newPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,passVo.getNewPass());
        UserDto user = userService.findByName(SecurityUtils.getCurrentUsername());
        if(!passwordEncoder.matches(oldPass, user.getPassword())){
            throw new BadRequestException("修改失败，旧密码错误");
        }
        if(passwordEncoder.matches(newPass, user.getPassword())){
            throw new BadRequestException("新密码不能与旧密码相同");
        }
        userService.updatePass(user.getUsername(),passwordEncoder.encode(newPass));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/updateAvatar")
    public ResponseEntity<Object> updateAvatar(@RequestParam MultipartFile avatar){
        return new ResponseEntity<>(userService.updateAvatar(avatar), HttpStatus.OK);
    }

    @Log("修改邮箱")
    @PostMapping(value = "/updateEmail/{code}")
    @ApiOperation(value = "修改邮箱")
    public ResponseEntity<Object> updateEmail(@PathVariable String code,@RequestBody User user) throws Exception {
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,user.getPassword());
        UserDto userDto = userService.findByName(SecurityUtils.getCurrentUsername());
        if(!passwordEncoder.matches(password, userDto.getPassword())){
            throw new BadRequestException("密码错误");
        }
        verifyService.validated(CacheKey.EMAIL_RESET_EMAIL_CODE + user.getEmail(), code);
        userService.updateEmail(userDto.getUsername(),user.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     */
    private void checkLevel(User resources) {
        Integer currentLevel =  Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
        Integer optLevel = roleService.findByRoles(resources.getRoles());
        if (currentLevel > optLevel) {
            throw new BadRequestException("角色权限不足");
        }
    }
}
