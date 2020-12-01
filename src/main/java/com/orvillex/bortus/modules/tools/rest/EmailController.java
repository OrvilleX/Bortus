package com.orvillex.bortus.modules.tools.rest;

import com.orvillex.bortus.annotation.Log;
import com.orvillex.bortus.modules.tools.domain.EmailConfig;
import com.orvillex.bortus.modules.tools.domain.vo.EmailVo;
import com.orvillex.bortus.modules.tools.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 邮件API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/email")
@Api(tags = "工具：邮件管理")
public class EmailController {
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<Object> queryConfig() {
        return new ResponseEntity<>(emailService.find(), HttpStatus.OK);
    }

    @Log("配置邮件")
    @PutMapping
    @ApiOperation("配置邮件")
    public ResponseEntity<Object> updateConfig(@Validated @RequestBody EmailConfig emailConfig) throws Exception {
        emailService.config(emailConfig,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("发送邮件")
    @PostMapping
    @ApiOperation("发送邮件")
    public ResponseEntity<Object> sendEmail(@Validated @RequestBody EmailVo emailVo){
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
