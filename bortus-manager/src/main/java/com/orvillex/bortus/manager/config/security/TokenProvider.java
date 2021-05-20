package com.orvillex.bortus.manager.config.security;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.orvillex.bortus.manager.utils.RedisUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 提供Token生成
 * @author y-z-f
 * @version 0.1
 */
@Component
public class TokenProvider implements InitializingBean {
    private final SecurityProperties properties;
    private final RedisUtils redisUtils;
    public static final String AUTHORITIES_KEY = "auth";
    private JwtParser jwtParser;
    private JwtBuilder jwtBuilder;

    public TokenProvider(SecurityProperties properties, RedisUtils redisUtils) {
        this.properties = properties;
        this.redisUtils = redisUtils;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getBase64Secret());
        Key key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        jwtBuilder = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512);
    }

    /**
     * 创建Token 设置永不过期，
     * Token 的时间有效性转到Redis 维护
     */
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return jwtBuilder
                .setId(IdUtil.simpleUUID())
                .claim(AUTHORITIES_KEY, authorities)
                .setSubject(authentication.getName())
                .compact();
    }

    /**
     * 创建Token
     */
    public String createToken(String authorities, String name) {
        return jwtBuilder
                .setId(IdUtil.simpleUUID())
                .claim(AUTHORITIES_KEY, authorities)
                .setSubject(name)
                .compact();
    }

    /**
     * 依据Token 获取鉴权信息
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // fix bug: 当前用户如果没有任何权限时，在输入用户名后，刷新验证码会抛IllegalArgumentException
        Object authoritiesStr = claims.get(AUTHORITIES_KEY);
        Collection<? extends GrantedAuthority> authorities =
                ObjectUtil.isNotEmpty(authoritiesStr) ?
                        Arrays.stream(authoritiesStr.toString().split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()) : Collections.emptyList();
        User principal = new User(claims.getSubject(), "******", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Claims getClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验token
     */
    public void checkRenewal(String token) {
        // 判断是否续期token,计算token的过期时间
        long time = redisUtils.getExpire(properties.getOnlineKey() + token) * 1000;
        Date expireDate = DateUtil.offset(new Date(), DateField.MILLISECOND, (int) time);
        // 判断当前时间与过期时间的时间差
        long differ = expireDate.getTime() - System.currentTimeMillis();
        // 如果在续期检查的范围内，则续期
        if (differ <= properties.getDetect()) {
            long renew = time + properties.getRenew();
            redisUtils.expire(properties.getOnlineKey() + token, renew, TimeUnit.MILLISECONDS);
        }
    }

    public String getToken(HttpServletRequest request) {
        final String requestHeader = request.getHeader(properties.getHeader());
        if (requestHeader != null && requestHeader.startsWith(properties.getTokenStartWith())) {
            return requestHeader.substring(7);
        }
        return null;
    }
}
