package com.example.redisdemo.service;

import jakarta.inject.Inject;
import com.example.redisdemo.utils.RedisClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@Named
@RequestScoped
public class UserServiceImpl {
    @Inject
    RedisClient redisClient;

    public void saveUser(String useId,String name){
        try(Jedis jedis = redisClient.getJedisPool().getResource();){
            jedis.hset("user:"+useId,"name",name);
        }
    }

    public String generateSessionId(String useId){
        String sessionId = UUID.randomUUID().toString();
        try(Jedis jedis = redisClient.getJedisPool().getResource()){
            jedis.setex("session:"+sessionId,60*60*24,sessionId);//1小时过期
        }
        return sessionId;
    }
}
