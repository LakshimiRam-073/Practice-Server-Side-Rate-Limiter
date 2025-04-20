import com.test.ratelimit.configuration.ConfigXMLParser;
import com.test.ratelimit.configuration.Configuration;
import com.test.ratelimit.limiters.RateLimiter;
import com.test.ratelimit.limiters.RateLimiterFactory;
import com.test.redis.RedisUtil;
import com.test.server.TestHttpServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {


        testServer();
        Thread.sleep(TimeUnit.SECONDS.toMillis(5));
//        testRedis();
//        testParseConfiguration();
        testLeakyBucket();


    }

    private static void testLeakyBucket() throws Exception {
        RateLimiter rateLimiter = RateLimiterFactory.getRateLimiter("/api/v1/login");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
        rateLimiter.allowRequest("localhost");
    }

    private static void testRedis() {
        RedisUtil.setValue("Harish","Mass");
        System.out.println(RedisUtil.getValue("Harish"));
        System.out.println(RedisUtil.exists("Harish"));
        RedisUtil.deleteKey("Harish");
        System.out.println(RedisUtil.getValue("Harish"));
        System.out.println(RedisUtil.exists("Harish"));
    }

    private static void testParseConfiguration() throws Exception {
        List<Configuration> configurationList = ConfigXMLParser.getConfigurationFromXML();
        for (Configuration conf : configurationList){
            System.out.println("-----------------------------------------");
            System.out.println(conf);
            System.out.println("-----------------------------------------\n");
        }
    }

    private static void testServer() throws Exception {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 101);
        userInfo.put("name", "Lakshimi Raman");
        userInfo.put("email", "lakshimi@example.com");
        userInfo.put("role", "admin");

        String uri = "/api/user/info";

        Map<String,Map<String,Object >> data = new HashMap<>();
        data.put(uri,userInfo);

        TestHttpServer.startTestServer(8080,data);
    }
}