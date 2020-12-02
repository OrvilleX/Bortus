package com.orvillex.bortus.modules.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OnlineUserService {
    public void save() {

    }

    public Map<String, Object> getAll(String filter, Pageable pageable) {
        return null;
    }

    public void kickOutForUsername(String userName) {

    }
}
