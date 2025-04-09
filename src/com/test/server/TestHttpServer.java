package com.test.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

        HttpServer testServer = HttpServer.create(new InetSocketAddress(port),0);


        for (String uri : uriVsData.keySet()){
            JSONObject data = convertMapToJSON(uriVsData.get(uri));

            testServer.createContext(uri,(HttpExchange exchange) ->{
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


    public static JSONObject convertMapToJSON(Map<String, Object> map) throws Exception {
        if (map == null) {
            return null;
        }
        JSONObject obj = new JSONObject();
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {

            String key = (String) iter.next();
            Object value = map.get(key);

            if (value instanceof List) {
                value = convertListToJSONArray((List) value);
            } else if (value instanceof Map) {
                value = convertMapToJSON((Map) value);
            }
            obj.put(key, value);
        }
        return obj;
    }

    public static Map<String, Object> convertJSONToMap(JSONObject obj) throws Exception {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> iter = obj.keys();

        while (iter.hasNext()) {
            String key = iter.next();
            Object value = obj.get(key);
            if (value instanceof JSONArray) {
                value = convertJSONArrayToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = convertJSONToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> convertJSONArrayToList(JSONArray array) throws Exception {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = convertJSONArrayToList((JSONArray) value);
            }

            else if (value instanceof JSONObject) {
                value = convertJSONToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static <T> JSONArray convertListToJSONArray(Collection<T> list) throws Exception {
        JSONArray arr = new JSONArray();
        for (Object value : list) {
            if (value instanceof List) {
                value = convertListToJSONArray((List) value);
            } else if (value instanceof Map) {
                value = convertMapToJSON((Map) value);
            }
            arr.put(value);
        }
        return arr;
    }
}
