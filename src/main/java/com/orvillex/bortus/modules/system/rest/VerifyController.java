package com.orvillex.bortus.modules.system.rest;

import com.orvillex.bortus.enums.CodeBiType;
import com.orvillex.bortus.modules.system.service.VerifyService;
import com.orvillex.bortus.modules.tools.domain.vo.EmailVo;
import com.orvillex.bortus.modules.tools.service.EmailService;
import com.orvillex.bortus.utils.CacheKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 验证服务API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
@Api(tags = "系统：验证码管理")
public class VerifyController {
    private final VerifyService verifyService;
    private final EmailService emailService;

    @PostMapping(value = "/resetEmail")
    @ApiOperation("重置邮箱，发送验证码")
    public ResponseEntity<Object> resetEmail(@RequestParam String email){
        EmailVo emailVo = verifyService.sendEmail(email, CacheKey.EMAIL_RESET_EMAIL_CODE);
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/email/resetPass")
    @ApiOperation("重置密码，发送验证码")
    public ResponseEntity<Object> resetPass(@RequestParam String email){
        EmailVo emailVo = verifyService.sendEmail(email, CacheKey.EMAIL_RESET_PWD_CODE);
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/validated")
    @ApiOperation("验证码验证")
    public ResponseEntity<Object> validated(@RequestParam String email, @RequestParam String code, @RequestParam Integer codeBi){
        CodeBiType biEnum = CodeBiType.find(codeBi);
        switch (Objects.requireNonNull(biEnum)){
            case ONE:
                verifyService.validated(CacheKey.EMAIL_RESET_EMAIL_CODE + email ,code);
                break;
            case TWO:
                verifyService.validated(CacheKey.EMAIL_RESET_PWD_CODE + email ,code);
                break;
            default:
                break;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
