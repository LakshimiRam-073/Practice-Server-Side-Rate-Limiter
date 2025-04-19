package com.test.ratelimit.limiters;

import com.test.ratelimit.configuration.Configuration;

public interface RateLimiter {

    /*
        I'm only trying with user as IP, could have done with without user . You know it is still the same
        Implementing...

        1. Leaky bucket
        2. Token bucket
        3. Fixed window
        4. Sliding window
        5. Moving window expiration


        Adding up 5 classes with one Factory class. so totally 6
     */
    boolean allowRequest(String ip) throws Exception; //with ip or user
//    boolean allowRequest(String uri) throws Exception; //with ip or user

}
