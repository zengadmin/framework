package com.fyqz.frameowrk.config;


import com.fyqz.frameowrk.cache.CacheUtil;
import com.fyqz.frameowrk.cache.Client;
import com.fyqz.frameowrk.cache.RedisHelper;
import com.fyqz.frameowrk.cache.RedissonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * Redis配置
 *
 * @author zengchao
 */
@Configuration
public class RedisConfig {
    @Autowired
    private RedisConnectionFactory factory;
    @Autowired
    private Client client;

    @Bean
    public RedisTemplate<Serializable, Serializable> redisTemplate() {
        RedisTemplate<Serializable, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setConnectionFactory(factory);
        RedisHelper redisHelper = new RedisHelper();
        redisHelper.setRedisTemplate(redisTemplate);
        CacheUtil.setCacheManager(redisHelper);
        RedissonHelper redissonHelper = new RedissonHelper();
        redissonHelper.setClient(client);
        return redisTemplate;
    }
}
