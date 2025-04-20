package com.test.ratelimit.limiters;

import com.test.ratelimit.configuration.Configuration;
import com.test.redis.RedisUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TockenBucket extends AbstractRateLimiter implements RateLimiter{

    private  static final String REDIS_TOKEN_BUCKET_KEY="token_bucket:";
    private static final String REDIS_TOKEN_BUCKET_LIMIT="tokens";
    public TockenBucket(Configuration configuration) {
        super(configuration);
    }

    @Override
    public boolean allowRequest(String ip) throws Exception {

        String key = REDIS_TOKEN_BUCKET_KEY+ip;
        String penalityKey = REDIS_PENALITY_KEY+ip;
        if (RedisUtil.exists(penalityKey)){
            System.out.println("Already in penality list for user "+ip);
            return  false;
        }

        String lastUpdateTImeString = RedisUtil.getSetValue(key,LAST_UPDATED_TIME);
        String bucketTokenStr = RedisUtil.getSetValue(key,REDIS_TOKEN_BUCKET_LIMIT);
        Long now = System.currentTimeMillis()/1000;

        if (Objects.isNull(bucketTokenStr) || Objects.isNull(lastUpdateTImeString)){
            Map<String,Object> fieldMap = new HashMap<>();
            fieldMap.put(LAST_UPDATED_TIME,now);
            if (Objects.isNull(configuration.getBurst())){
                configuration.setBurst(Configuration.DEFAULT_BURST);
            }
            fieldMap.put(REDIS_TOKEN_BUCKET_LIMIT,configuration.getBurst() + configuration.getLimit());
            lastUpdateTImeString = String.valueOf(now);
            bucketTokenStr = String.valueOf(configuration.getBurst() +configuration.getLimit());
        }


        Integer tokens = Integer.parseInt(bucketTokenStr);
        Long refilTime = Long.parseLong(lastUpdateTImeString);

        Long elapsed = now - refilTime;

        if (elapsed > configuration.getWindow()){
            tokens = configuration.getBurst() + configuration.getLimit();
        }
        if (tokens < 1){
            String message = "Penalized for using URI:"+configuration.getUri()+" for more than "+configuration.getLimit();
            RedisUtil.setValue(penalityKey,message,configuration.getPenalty());
            System.out.println(message);
            return false;
        }

        tokens--;
        Map<String,Object> fieldMap = new HashMap<>();
        fieldMap.put(LAST_UPDATED_TIME,now);
        if (Objects.isNull(configuration.getBurst())){
            configuration.setBurst(Configuration.DEFAULT_BURST);
        }
        fieldMap.put(REDIS_TOKEN_BUCKET_LIMIT,tokens);
        RedisUtil.putSet(fieldMap,key,Objects.nonNull(configuration.getTtl()) ? configuration.getTtl() : RedisUtil.DEFAULT_TTL_SECONDS);
        System.out.println("Allowed access to user "+ ip + " with Remaining request of "+(tokens));

        return true;
    }
}
