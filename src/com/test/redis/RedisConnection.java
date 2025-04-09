package com.test.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

public class RedisConnection {
    private  static  RedisConnection instance;
    private static JedisPool jedisPool;
    private static final String HOST="localhost";
    private static final Integer DEFAULT_PORT=6379;


    protected RedisConnection(String host,Integer port){
        this.jedisPool= new JedisPool(host,port);
    }


    public static synchronized RedisConnection getInstance() {
        if (Objects.isNull(instance)){
            instance = new RedisConnection(HOST,DEFAULT_PORT);
        }
        return  instance;
    }

    public Jedis getJedis(){
       return jedisPool.getResource();
    }

    public void close(){
        jedisPool.close();
    }

}
