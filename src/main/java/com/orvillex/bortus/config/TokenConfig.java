package com.orvillex.bortus.config;

import com.orvillex.bortus.config.security.SecurityProperties;
import com.orvillex.bortus.config.security.TokenProvider;
import com.orvillex.bortus.handler.TokenFilter;
import com.orvillex.bortus.modules.security.service.OnlineUserService;
import com.orvillex.bortus.modules.security.service.UserCacheClean;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 基于JWT的配置
 */
@RequiredArgsConstructor
public class TokenConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    private final SecurityProperties properties;
    private final OnlineUserService onlineUserService;
    private final UserCacheClean userCacheClean;

    @Override
    public void configure(HttpSecurity http) {
        TokenFilter customFilter = new TokenFilter(tokenProvider, properties, onlineUserService, userCacheClean);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
