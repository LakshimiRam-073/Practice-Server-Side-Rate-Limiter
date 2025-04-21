package com.test.ratelimit.limiters;

import com.test.ratelimit.configuration.Configuration;
import com.test.redis.RedisUtil;

public class FixedWindow extends AbstractRateLimiter implements RateLimiter{
    private  static final String REDIS_KEY_FIXED_WINDOW="fixed_window:";
    public FixedWindow(Configuration configuration) {
        super(configuration);
    }

    @Override
    public boolean allowRequest(String ip) throws Exception {

        String key = REDIS_KEY_FIXED_WINDOW+ip;
        if (hasPenality(ip)){
            return false;
        }
        Long count = RedisUtil.increment(key);
        if (count  == 1){
            System.out.println("First time going for redis");
            RedisUtil.expire(key,configuration.getTtl());
        }

        Integer allowedLimit = configuration.getLimit() + configuration.getBurst();

        if (count > allowedLimit){
            markForPenality(ip);
            return false;
        }


        System.out.println("Allowed: IP=" + ip + " Count=" + count);
        return true;

    }
}
