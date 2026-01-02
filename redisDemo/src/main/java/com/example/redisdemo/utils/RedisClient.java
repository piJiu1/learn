package com.example.redisdemo.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@ApplicationScoped
public class RedisClient {
    private JedisPool jedisPool;
    @PostConstruct
    public void init(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(64);
        poolConfig.setMaxTotal(128);
        //poolConfig.setMaxWaitMillis(5000);
        poolConfig.setMinIdle(10);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        // 假设 Redis 运行在 localhost:6379，无密码
        // 若有密码、TLS、ACL 用户，请调整参数
        this.jedisPool = new JedisPool(poolConfig,"localhost",6379,2000);
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }
    @PreDestroy
    public void destroy(){
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
