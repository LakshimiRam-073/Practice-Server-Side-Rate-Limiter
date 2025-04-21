package com.test.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;

import com.test.ratelimit.configuration.ConfigurationRefresher;
import com.test.ratelimit.limiters.RateLimiter;
import com.test.ratelimit.limiters.RateLimiterFactory;
import com.test.redis.RedisConnection;
import org.json.JSONObject;

import static com.test.util.JsonUtil.convertMapToJSON;


public class TestHttpServer {

    public static final Integer DEFAULT_PORT = 8080;
    public static final String CONTENT_TYPE="Content-Type";
    public static final String JSON_FORMAT="application/json";
    public static final Integer SUCCESS_CODE=200;




    public static void startTestServer(Map<String,Map<String,Object>> uriVsData) throws  Exception{
        startTestServer(DEFAULT_PORT,uriVsData);
    }
    public static void startTestServer(Integer port, Map<String,Map<String,Object>> uriVsData)throws  Exception{
        if (Objects.isNull(port)){
            port = DEFAULT_PORT;
        }
        ConfigurationRefresher.start();

        HttpServer testServer = HttpServer.create(new InetSocketAddress(port),0);


        for (String uri : uriVsData.keySet()){
            JSONObject data = convertMapToJSON(uriVsData.get(uri));

            testServer.createContext(uri,(HttpExchange exchange) ->{

                String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();
                try {
                    RateLimiter rateLimiter = RateLimiterFactory.getRateLimiter(uri);
                    if (!rateLimiter.allowRequest(clientIP)){

                        String rateLimitExceeded = "Rate limit exceeded for " + clientIP;
                        exchange.sendResponseHeaders(429, rateLimitExceeded.getBytes().length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(rateLimitExceeded.getBytes());
                        }
                        return;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                String response = data.toString();
                    exchange.getResponseHeaders().set(CONTENT_TYPE,JSON_FORMAT);
                    exchange.sendResponseHeaders(SUCCESS_CODE,response.getBytes().length);
                    try(OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
            });
        }

        testServer.setExecutor(null); // Use default executor
        testServer.start();
        System.out.println("✅ Mock API server is running at http://localhost:"+port);


    }



}
