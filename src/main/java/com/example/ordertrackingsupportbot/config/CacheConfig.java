package com.example.ordertrackingsupportbot.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Spring simple cache (ConcurrentHashMap-backed) is auto-configured
    // For production: swap to Redis with spring-boot-starter-data-redis
}

