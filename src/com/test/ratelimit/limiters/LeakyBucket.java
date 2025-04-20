package com.test.ratelimit.limiters;

import com.test.ratelimit.configuration.Configuration;
import com.test.ratelimit.configuration.ConfigurationUtil;
import com.test.redis.RedisUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class LeakyBucket extends AbstractRateLimiter implements RateLimiter{

    //TTL is not mandatory heere
    private  static final  String REDIS_LEAKY_BUCKET_KEY = "leaky_bucket:";
    private  static final String REDIS_LEAKY_BUCKET_LEVEL ="level";
    private static final Integer DEFAULT_BURST_LEVEL =1;

    public LeakyBucket(Configuration configuration) {
        super(configuration);
    }

    @Override
    public boolean allowRequest( String ip) throws Exception {
        String key = REDIS_LEAKY_BUCKET_KEY + ip;
        String penalityKey = REDIS_PENALITY_KEY+ip;
        if (RedisUtil.exists(penalityKey)){
            System.out.println("Already in penality list for user "+ip);
            return  false;
        }
        String lastUpdatedTimeStr = RedisUtil.getSetValue(key,LAST_UPDATED_TIME);
        String levelStr = RedisUtil.getSetValue(key,REDIS_LEAKY_BUCKET_LEVEL);
        Long currentTime = System.currentTimeMillis()/1000;

        //first time
        if (Objects.isNull(lastUpdatedTimeStr) || Objects.isNull(levelStr)){

            Map<String, Object> fieldmap = new HashMap<>();
            fieldmap.put(LAST_UPDATED_TIME, currentTime);
            if (Objects.nonNull(configuration.getBurst())){
                configuration.setBurst(DEFAULT_BURST_LEVEL);
            }
            fieldmap.put(REDIS_LEAKY_BUCKET_LEVEL, configuration.getBurst());
            RedisUtil.putSet(fieldmap,key);
            lastUpdatedTimeStr = String.valueOf(currentTime);
            levelStr = String.valueOf(configuration.getBurst());
        }


        //leaky algo
        Long lastUpdatedTime = Long.parseLong(lastUpdatedTimeStr);
        Integer level = Integer.parseInt(levelStr);
        Long elapsed = currentTime - lastUpdatedTime;
        if (elapsed > configuration.getWindow()){
            level = configuration.getBurst();
        }

        if (level + 1 > configuration.getLimit()){
            String message = "Penalized for using URI:"+configuration.getUri()+" for more than "+configuration.getLimit();
            RedisUtil.setValue(penalityKey,message,configuration.getPenalty());
            System.out.println(message);
            return false;
        }

        Map<String, Object> fieldmap = new HashMap<>();
        fieldmap.put(LAST_UPDATED_TIME, currentTime);
        if (Objects.nonNull(configuration.getBurst())){
            configuration.setBurst(DEFAULT_BURST_LEVEL);
        }
        fieldmap.put(REDIS_LEAKY_BUCKET_LEVEL, level+1);
        RedisUtil.putSet(fieldmap,key,Objects.isNull(configuration.getTtl()) ? RedisUtil.DEFAULT_TTL_SECONDS : configuration.getTtl());
        System.out.println("Allowed access to user "+ ip + " with Remaining request of "+(configuration.getLimit() -(level+1)));




        System.out.println("LEVEL "+RedisUtil.getSetValue(key,REDIS_LEAKY_BUCKET_LEVEL) );
        return true;
    }
}
