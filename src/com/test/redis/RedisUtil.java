package com.test.redis;
import com.test.ratelimit.configuration.Configuration;
import com.test.ratelimit.configuration.ConfigurationRefresher;
import com.test.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.test.ratelimit.configuration.ConfigurationUtil.buildConfigurationFromJson;

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

    public static JSONArray getJson(String prefix) throws Exception{
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams scanParams = new ScanParams().match(prefix+"*").count(100);
        JSONArray array = new JSONArray();
        try(Jedis jedis = RedisConnection.getInstance().getJedis()){

            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                for (String key : scanResult.getResult()) {
                    String jsonValue = RedisUtil.getValue(key);
                    JSONObject jsonObject = JsonUtil.safeParse(jsonValue);
                    jsonObject.put("key",key);
                    array.put(jsonObject);

                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        }
        return array;
    }

    public static boolean ping(){
        try (Jedis jedis = RedisConnection.getInstance().getJedis()) {
             jedis.ping();
             return true;
        }catch (Exception e){
            return false;
        }

    }




}
