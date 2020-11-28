package com.orvillex.bortus.modules.tools.rest;

import com.orvillex.bortus.modules.tools.service.EmailService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
