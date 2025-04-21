package com.test.ratelimit.limiters;

import com.test.ratelimit.configuration.Configuration;
import com.test.ratelimit.configuration.ConfigurationUtil;

public class RateLimiterFactory {


    public  static RateLimiter getRateLimiter(String uri) throws Exception{

        if (!AbstractRateLimiter.isURIExists(uri)){
            System.out.println("URI doesn't exists so no limiting");
            return null;
        }
        Configuration configuration = ConfigurationUtil.getConfiguration(uri);

        switch (configuration.getAlgoirthm()){

            case LEAKY_BUCKET:
                return new LeakyBucket(configuration);

            case TOKEN_BUCKET:
                return new TockenBucket(configuration);

            case FIXED_WINDOW:
                return new FixedWindow(configuration);

            case MOVING_WINDOW_EXPIRATION:
                return new MovingWindowExpiration(configuration);

            default:
                System.out.println("Not implemented ");
                return null;
        }

    }
}
