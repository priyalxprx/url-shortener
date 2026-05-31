package com.project.url_shortener.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String shortCode, String originalUrl, long daysToExpire) {
        redisTemplate.opsForValue()
            .set("url:" + shortCode, originalUrl, daysToExpire, TimeUnit.DAYS);
    }

    public String get(String shortCode) {
        return redisTemplate.opsForValue().get("url:" + shortCode);
    }

    public void delete(String shortCode) {
        redisTemplate.delete("url:" + shortCode);
    }
}