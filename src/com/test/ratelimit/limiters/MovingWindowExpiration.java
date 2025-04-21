package com.test.ratelimit.limiters;

import com.test.ratelimit.configuration.Configuration;
import com.test.redis.RedisConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MovingWindowExpiration extends AbstractRateLimiter implements RateLimiter{
    private static final String REDIS_ROLLING_WINDOW="rolling_window:";
    public MovingWindowExpiration(Configuration configuration) {
        super(configuration);
    }

    @Override
    public boolean allowRequest(String ip) throws Exception {

        String key = REDIS_ROLLING_WINDOW+ip;
        if (hasPenality(ip)){
            return false;
        }

        Long currentTime = System.currentTimeMillis();
        Long countRemain = 0L;

        Transaction tx = null;


        try (Jedis jedis = RedisConnection.getInstance().getJedis()) {
            // 1. Remove old entries
            jedis.zremrangeByScore(key, 0, TimeUnit.SECONDS.toMillis(configuration.getWindow()));

            // 2. Get current count (after removal)
            long currentCount = jedis.zcard(key);

            System.out.println(jedis.zrange(key,0,-1));

            // 3. Check limit
            long allowed = configuration.getLimit() + configuration.getBurst();
            if (currentCount >= allowed) {
                markForPenality(ip);
                return false;
            }

            // 4. Add current timestamp only if under limit
            jedis.zadd(key, currentTime, String.valueOf(currentTime));
            jedis.expire(key, configuration.getTtl());

            countRemain = allowed - currentCount - 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("Successfully accepted connection for IP " + ip +
                " for URI " + configuration.getUri() +
                " with remaining count of " + countRemain);

        return true;
    }
}
