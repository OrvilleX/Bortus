package com.orvillex.bortus.config;

import cn.hutool.core.lang.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.extern.slf4j.Slf4j;
import com.orvillex.bortus.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Redis缓存配置
 * 
 * @author y-z-f
 * @version 0.1
 */
@Slf4j
@Configuration
@EnableCaching
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 设置缓存序列化方式以及缓存过期时间
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer))
                .entryTtl(Duration.ofHours(2));
        return configuration;
    }

    @SuppressWarnings("all")
    @Bean(name = "redisTemplate")
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            Map<String, Object> container = new HashMap<>(3);
            Class<?> targetClassClass = target.getClass();
            container.put("class", targetClassClass.toGenericString());
            container.put("methodName", method.getName());
            container.put("package", targetClassClass.getPackage());
            for (int i = 0; i < params.length; i++) {
                container.put(String.valueOf(i), params[i]);
            }
            String jsonString = JSON.toJSONString(container);
            return DigestUtils.sha256Hex(jsonString);
        };
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        log.info("init -> [{}]", "Redis CacheErrorHandler");
        return new CacheErrorHandler(){
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error("Redis occur get error: key -> [{}]", key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("Redis occur put error: key -> [{}]", key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("Redis occur evict error: key -> [{}]", key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error("Redis occur clear error", exception);
            }
        };
    };

    /**
     * 值序列化
     */
    class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

        private final Class<T> clazz;

        FastJsonRedisSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }
            return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length <= 0) {
                return null;
            }
            String str = new String(bytes, StandardCharsets.UTF_8);
            return JSON.parseObject(str, clazz);
        }
    }

    /**
     * 键序列化
     */
    class StringRedisSerializer implements RedisSerializer<Object> {

        private final Charset charset;

        StringRedisSerializer() {
            this(StandardCharsets.UTF_8);
        }

        private StringRedisSerializer(Charset charset) {
            Assert.notNull(charset, "Charset must not be null!");
            this.charset = charset;
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            return (bytes == null ? null : new String(bytes, charset));
        }

        @Override
        public byte[] serialize(Object t) throws SerializationException {
            String string = JSON.toJSONString(t);
            if (StringUtils.isBlank(string)) {
                return null;
            }
            string = string.replace("\"", "");
            return string.getBytes(charset);
        }
    }
}
