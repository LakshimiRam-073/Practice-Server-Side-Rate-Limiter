package com.test.ratelimit.limiters;

import com.test.ratelimit.configuration.Configuration;
import com.test.ratelimit.configuration.ConfigurationUtil;

public class LeakyBucket implements RateLimiter{

    @Override
    public boolean allowRequest(String uri, String ip) throws Exception {
        Configuration configuration = ConfigurationUtil.getConfiguration(uri);
        
        return false;
    }
}
