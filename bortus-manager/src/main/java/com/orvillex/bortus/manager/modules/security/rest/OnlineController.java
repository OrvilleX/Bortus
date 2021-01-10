package com.orvillex.bortus.manager.modules.security.rest;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.utils.EncryptUtils;
import com.orvillex.bortus.manager.modules.security.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 在线用户管理API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/online")
public class OnlineController {
    private final OnlineUserService onlineUserService;

    @GetMapping
    @PreAuthorize("@x.check()")
    public ResponseEntity<Object> query(String filter, Pageable pageable){
        return new ResponseEntity<>(onlineUserService.getAll(filter, pageable), HttpStatus.OK);
    }

    @Log("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@x.check()")
    public void download(HttpServletResponse response, String filter) throws IOException {
        onlineUserService.download(onlineUserService.getAll(filter), response);
    }

    @DeleteMapping
    @PreAuthorize("@x.check()")
    public ResponseEntity<Object> delete(@RequestBody Set<String> keys) throws Exception {
        for (String key : keys) {
            key = EncryptUtils.desDecrypt(key);
            onlineUserService.kickOut(key);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
