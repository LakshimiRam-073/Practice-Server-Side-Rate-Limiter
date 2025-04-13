package com.test.util;

import com.test.ratelimit.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class JsonUtil {



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

    public static JSONObject safeParse(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(content);
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject configurationAsJson(Configuration configuration) throws Exception{

        Map<String,Object> confAsMap = new HashMap<>();
        confAsMap.put(Configuration.RLConfig.URI.getConfig(),configuration.getLimit());
        confAsMap.put(Configuration.RLConfig.LIMIT.getConfig(),configuration.getLimit());
        confAsMap.put(Configuration.RLConfig.WINDOW.getConfig(), configuration.getWindow());
        confAsMap.put(Configuration.RLConfig.BURST.getConfig(), configuration.getBurst());
        confAsMap.put(Configuration.RLConfig.TTL.getConfig(),configuration.getTtl());
        confAsMap.put(Configuration.RLConfig.PENALTY_TIME.getConfig(), configuration.getPenalty());

        confAsMap.put(Configuration.RLConfig.LIMIT_UNIT.getConfig(), configuration.getLimitUnit());
        confAsMap.put(Configuration.RLConfig.WINDOW_UNIT.getConfig(), configuration.getWindowUnit());
        confAsMap.put(Configuration.RLConfig.TTL_UNIT.getConfig(), configuration.getTtlUnit());
        confAsMap.put(Configuration.RLConfig.PENALTY_TIME_UNIT.getConfig(), configuration.getPenaltyUnit());
        return convertMapToJSON(confAsMap);

    }


}
