package com.test.redis;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class RedisUtil {


    private static final Integer DEFAULT_TTL_SECONDS= 10;

    public static void setValue(String key, String value) {
        setValue(key,value,DEFAULT_TTL_SECONDS);
    }
    public static void setValue(String key, String value, int ttlSeconds) {
        try (Jedis jedis = RedisConnection.getInstance().getJedis()) {
            jedis.set(key, value);
            if (ttlSeconds > 0) {
                jedis.expire(key, ttlSeconds);
            }
        }
    }

    public static String getValue(String key) {
        try (Jedis jedis = RedisConnection.getInstance().getJedis()) {
            return jedis.get(key);
        }
    }

    public static long increment(String key) {
        try (Jedis jedis = RedisConnection.getInstance().getJedis()) {
            return jedis.incr(key);
        }
    }

    public static void deleteKey(String key) {
        try (Jedis jedis = RedisConnection.getInstance().getJedis()) {
            jedis.del(key);
        }
    }

    public static boolean exists(String key) {
        try (Jedis jedis = RedisConnection.getInstance().getJedis()) {
            return jedis.exists(key);
        }
    }
}
