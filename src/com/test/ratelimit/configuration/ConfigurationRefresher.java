package com.test.ratelimit.configuration;

import com.test.redis.RedisUtil;
import org.json.JSONObject;


import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import static com.test.util.JsonUtil.configurationAsJson;


public class ConfigurationRefresher{

    public static final String REDIS_PREFIX_STRING="ratelimiter_config:";
    private static final Integer REDIS_EXPIRATION_SECONDS =90;
    private static final Integer REFRESH_INTERVAL=60;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public static void start() throws Exception{


        Runnable task = () ->{

        try {
            if (!RedisUtil.ping()){
                throw new Exception("Redis is not pinging..... NO PONG YOU idiot");
            }
            List<Configuration> configurationList = ConfigXMLParser.getConfigurationFromXML();
            for (Configuration conf : configurationList){
                JSONObject confJson = configurationAsJson(conf);
                String key = REDIS_PREFIX_STRING + conf.getUri();
                RedisUtil.setValue(key,confJson.toString(), REDIS_EXPIRATION_SECONDS);
                //LOGGER
                System.out.println("Configuration has been refreshed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Refreshing Configurations exited with non zero.... Check Redis connections");
        }

        };

        scheduler.scheduleAtFixedRate(task, 0, REFRESH_INTERVAL, TimeUnit.SECONDS);

    }




}
