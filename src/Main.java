import com.test.redis.RedisUtil;
import com.test.server.TestHttpServer;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {


//        testServer();
//        testRedis();

    }

    private static void testRedis() {
        RedisUtil.setValue("Harish","Mass");
        System.out.println(RedisUtil.getValue("Harish"));
        System.out.println(RedisUtil.exists("Harish"));
        RedisUtil.deleteKey("Harish");
        System.out.println(RedisUtil.getValue("Harish"));
        System.out.println(RedisUtil.exists("Harish"));
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