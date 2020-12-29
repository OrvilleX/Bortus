package com.orvillex.bortus.manager.modules.tools.rest;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.modules.tools.domain.EmailConfig;
import com.orvillex.bortus.manager.modules.tools.domain.vo.EmailVo;
import com.orvillex.bortus.manager.modules.tools.service.EmailService;
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
public class EmailController {
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<Object> queryConfig() {
        return new ResponseEntity<>(emailService.find(), HttpStatus.OK);
    }

    @Log("配置邮件")
    @PutMapping
    public ResponseEntity<Object> updateConfig(@Validated @RequestBody EmailConfig emailConfig) throws Exception {
        emailService.config(emailConfig,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("发送邮件")
    @PostMapping
    public ResponseEntity<Object> sendEmail(@Validated @RequestBody EmailVo emailVo){
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
