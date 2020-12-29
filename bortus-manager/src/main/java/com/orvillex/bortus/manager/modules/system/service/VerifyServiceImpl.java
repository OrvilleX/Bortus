package com.orvillex.bortus.manager.modules.system.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.orvillex.bortus.manager.utils.RedisUtils;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.tools.domain.vo.EmailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 验证服务实现
 * @author y-z-f
 * @version 0.1
 */
@Service
@RequiredArgsConstructor
public class VerifyServiceImpl implements VerifyService {
    @Value("${code.expiration}")
    private Long expiration;
    private final RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailVo sendEmail(String email, String key) {
        EmailVo emailVo;
        String content;
        String redisKey = key + email;
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email/email.ftl");
        Object oldCode = redisUtils.get(redisKey);
        if (oldCode == null) {
            String code = RandomUtil.randomNumbers(6);
            if (!redisUtils.set(redisKey, code, expiration)) {
                throw new BadRequestException("服务异常");
            }
            content = template.render(Dict.create().set("code", code));
            emailVo = new EmailVo(Collections.singletonList(email), "Bortus数据平台", content);
        } else {
            content = template.render(Dict.create().set("code", oldCode));
            emailVo = new EmailVo(Collections.singletonList(email), "Bortus数据平台", content);
        }
        return emailVo;
    }

    @Override
    public void validated(String key, String code) {
        Object value = redisUtils.get(key);
        if (value == null || !value.toString().equals(code)) {
            throw new BadRequestException("无效验证码");
        } else {
            redisUtils.delete(key);
        }
    }
}
