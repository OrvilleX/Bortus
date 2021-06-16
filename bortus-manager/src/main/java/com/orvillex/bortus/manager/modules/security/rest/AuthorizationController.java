package com.orvillex.bortus.manager.modules.security.rest;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.orvillex.bortus.manager.annotation.AnonymousDeleteMapping;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.annotation.AnonymousGetMapping;
import com.orvillex.bortus.manager.annotation.AnonymousPostMapping;
import com.orvillex.bortus.manager.config.RsaProperties;
import com.orvillex.bortus.manager.config.security.LoginProperties;
import com.orvillex.bortus.manager.config.security.TokenProvider;
import com.orvillex.bortus.manager.enums.LoginCodeType;
import com.orvillex.bortus.manager.modules.security.service.dto.AuthUserDto;
import com.orvillex.bortus.manager.modules.security.service.dto.JwtUserDto;
import com.orvillex.bortus.manager.modules.system.domain.WxUser;
import com.orvillex.bortus.manager.modules.system.domain.vo.LoginByCodeVo;
import com.orvillex.bortus.manager.modules.system.domain.vo.LoginCodeReply;
import com.orvillex.bortus.manager.modules.system.service.WxUserService;
import com.orvillex.bortus.manager.modules.system.service.dto.DeptSmallDto;
import com.orvillex.bortus.manager.modules.system.service.dto.UserDto;
import com.orvillex.bortus.manager.utils.RedisUtils;
import com.orvillex.bortus.manager.utils.RsaUtils;
import com.orvillex.bortus.manager.utils.SecurityUtils;
import com.orvillex.bortus.manager.utils.StringUtils;
import com.orvillex.bortus.manager.config.security.SecurityProperties;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.security.event.WxRegisterEvent;
import com.orvillex.bortus.manager.modules.security.service.OnlineUserService;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 授权服务API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@Api(tags = "授权服务")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {
    private final SecurityProperties properties;
    private final RedisUtils redisUtils;
    private final OnlineUserService onlineUserService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final WxUserService wxUserService;
    private final WxMaService wxMaService;
    private final ApplicationEventPublisher publisher;

    @Resource
    private LoginProperties loginProperties;

    @Log("微信登录")
    @AnonymousPostMapping(value = "wxlogin")
    @ApiOperation(value = "微信登录")
    public ResponseEntity<Object> wxlogin(@RequestBody LoginByCodeVo loginCode, HttpServletRequest request) {
        String openId = null;
        String sessionKey = null;
        String unionId = null;
        try {
            WxMaJscode2SessionResult result = wxMaService.getUserService().getSessionInfo(loginCode.getCode());
            sessionKey = result.getSessionKey();
            openId = result.getOpenid();
            unionId = result.getUnionid();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (sessionKey == null || openId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LoginCodeReply reply = new LoginCodeReply();
        WxUser wxUser = wxUserService.findByWxOpenId(openId);
        if (wxUser == null) {
            wxUser = new WxUser();
            wxUser.setWxOpenId(openId);
            wxUser.setCity(loginCode.getCity());
            wxUser.setEncryptedData(loginCode.getEncryptedData());
            wxUser.setGender(loginCode.getGender());
            wxUser.setHeadUrl(loginCode.getAvatarUrl());
            wxUser.setIv(loginCode.getIv());
            wxUser.setNickName(loginCode.getNickName());
            wxUser.setProvince(loginCode.getProvince());
            wxUser.setSource(1l);
            wxUser.setSessionKey(sessionKey);
            Long userId = wxUserService.create(wxUser);
            wxUser.setId(userId);
            WxRegisterEvent event = new WxRegisterEvent(wxUser);
            publisher.publishEvent(event);
            reply.setNew(true);
        } else {
            wxUser.setSessionKey(sessionKey);
            wxUserService.update(wxUser);
            reply.setNew(false);
        }

        String token = tokenProvider.createToken("tenant", "#wx" + sessionKey);
        DeptSmallDto dept = new DeptSmallDto();
        dept.setName("微信");
        UserDto userDto = new UserDto();
        userDto.setId(wxUser.getId());
        userDto.setUsername(wxUser.getSessionKey());
        userDto.setAvatarPath(wxUser.getHeadUrl());
        userDto.setEmail(wxUser.getEmail());
        userDto.setNickName(wxUser.getNickName());
        userDto.setDept(dept);

        List<Long> dataScopes = new ArrayList<>();
        List<GrantedAuthority> auth = new ArrayList<>();
        JwtUserDto jwtUserDto = new JwtUserDto(
            userDto,
            dataScopes,
            auth
        );
        onlineUserService.save(jwtUserDto, token, request);

        reply.setToken(token);
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @Log("用户登录")
    @AnonymousPostMapping(value = "/login")
    @ApiOperation(value = "用户登录")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        // 密码解密
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
        // 查询验证码
        String code = (String) redisUtils.get(authUser.getUuid());
        // 清除验证码
        redisUtils.delete(authUser.getUuid());
        if (StringUtils.isBlank(code)) {
            throw new BadRequestException("验证码不存在或已过期");
        }
        if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            throw new BadRequestException("验证码错误");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌
        String token = tokenProvider.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        if (loginProperties.isSingleLogin()) {
            //踢掉之前已经登录的token
            onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
        }
        return ResponseEntity.ok(authInfo);
    }

    @GetMapping(value = "/info")
    public ResponseEntity<Object> getUserInfo() {
        return ResponseEntity.ok(SecurityUtils.getCurrentUser());
    }

    @AnonymousGetMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeType.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisUtils.set(uuid, captchaValue, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    @AnonymousDeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        onlineUserService.logout(tokenProvider.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
