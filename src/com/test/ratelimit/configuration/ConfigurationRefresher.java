package com.test.ratelimit.configuration;

import com.test.redis.RedisConnection;
import com.test.redis.RedisUtil;
import org.json.JSONObject;


import java.util.List;
import java.util.Objects;
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
                throw new RedisConnection.RedisConnectionException("Redis is not pinging..... NO PONG YOU idiot");

            }
            List<Configuration> configurationList = ConfigXMLParser.getConfigurationFromXML();
            for (Configuration conf : configurationList){
                JSONObject confJson = configurationAsJson(conf);
                String key = REDIS_PREFIX_STRING + conf.getUri();
                RedisUtil.setValue(key,confJson.toString(), REDIS_EXPIRATION_SECONDS);


            }
                System.out.println("Configuration has been refreshed,Checking  for configurations");
                if (!configurationList.isEmpty()){
                    Configuration confTest = configurationList.get(0);
                    String keyTest = REDIS_PREFIX_STRING + confTest.getUri();
                    String confValue = RedisUtil.getValue(keyTest);
                    if (Objects.isNull(confValue) || "".equals(confValue)){
                        throw new Exception("Refreshing is not happened properly");
                    }
                    System.out.println("Configurations added properly");

                }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Refreshing Configurations exited with non zero.... Check Redis connections");
            System.exit(123);
        }

        };

        scheduler.scheduleAtFixedRate(task, 0, REFRESH_INTERVAL, TimeUnit.SECONDS);

    }




}
