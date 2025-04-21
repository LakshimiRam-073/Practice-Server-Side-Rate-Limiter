package com.test.ratelimit.limiters;

import com.test.ratelimit.configuration.ConfigXMLParser;
import com.test.ratelimit.configuration.Configuration;
import com.test.redis.RedisUtil;

import static com.test.ratelimit.configuration.ConfigurationRefresher.REDIS_PREFIX_STRING;

public abstract class AbstractRateLimiter implements RateLimiter {
    Configuration configuration;
    protected static final String REDIS_PENALITY_KEY="penality:";


    public AbstractRateLimiter(Configuration configuration) {
        this.configuration = configuration;
    }

    protected static final String LAST_UPDATED_TIME="last_update_time";
    public static boolean isURIExists(String uri) throws Exception{
        String keyTest = REDIS_PREFIX_STRING + uri;
        return RedisUtil.exists(keyTest);
    }
    public  boolean hasPenality(String ip){
        String penalityKey = REDIS_PENALITY_KEY+ip;
        if (RedisUtil.exists(penalityKey)){
            System.out.println("Already in penality list for user "+ip);
            return  true;

        }
        return false;

    }

    void markForPenality(String ip) {
        String penalityKey = REDIS_PENALITY_KEY+ip;
        String message = "Penalized for using URI:"+configuration.getUri()+" for more than "+configuration.getLimit();
        RedisUtil.setValue(penalityKey,message,configuration.getPenalty());
        System.out.println(message);

    }

    @Override
    public abstract boolean allowRequest(String ip) throws Exception ;
}
