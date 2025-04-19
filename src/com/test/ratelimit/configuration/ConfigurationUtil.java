package com.test.ratelimit.configuration;

import com.test.redis.RedisUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ConfigurationUtil {


    public static List<Configuration> getAllConfigurations() throws Exception {
        JSONArray jsonArray = RedisUtil.getJson(ConfigurationRefresher.REDIS_PREFIX_STRING); // Fetch from Redis
        List<Configuration> confs = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Configuration conf = buildConfigurationFromJson(jsonObject);
            confs.add(conf);
        }

        return confs;
    }
    public static Configuration getConfiguration(String uri) throws Exception {
        String key = ConfigurationRefresher.REDIS_PREFIX_STRING + uri;
        String jsonString = RedisUtil.getValue(key);
        if (jsonString == null) return null;
        JSONObject jsonObject = new JSONObject(jsonString);
        return buildConfigurationFromJson(jsonObject);
    }

    static Configuration makeConfigurations(Element nodeElement) throws  Exception{
        //must configuration
        String uri = requireConfig(nodeElement, Configuration.RLConfig.URI);
        Integer limit = parseRequiredInt(nodeElement, Configuration.RLConfig.LIMIT);
        Integer window = parseRequiredInt(nodeElement, Configuration.RLConfig.WINDOW);

        Configuration conf = new Configuration(uri, limit, window);

        // Optional parameters
        parseOptionalInt(nodeElement, Configuration.RLConfig.BURST,Configuration.DEFAULT_BURST).ifPresent(conf::setBurst);
        parseOptionalInt(nodeElement, Configuration.RLConfig.TTL,Configuration.DEFAULT_TTL).ifPresent(conf::setTtl);
        parseOptionalInt(nodeElement, Configuration.RLConfig.PENALTY_TIME, -1 ).ifPresent(conf::setPenalty);

        // Unit parameters (optional – if you want to use them) only used if the above config time is given
        parseOptionalTimeUnit(nodeElement, Configuration.RLConfig.LIMIT_UNIT,Configuration.DEFAULT_TIME_UNIT).ifPresent(conf::setLimitUnit);
        parseOptionalTimeUnit(nodeElement, Configuration.RLConfig.WINDOW_UNIT,Configuration.DEFAULT_TIME_UNIT).ifPresent(conf::setWindowUnit);
        parseOptionalTimeUnit(nodeElement, Configuration.RLConfig.TTL_UNIT,Configuration.DEFAULT_TIME_UNIT).ifPresent(conf::setTtlUnit);
        parseOptionalTimeUnit(nodeElement, Configuration.RLConfig.PENALTY_TIME_UNIT,Configuration.DEFAULT_TIME_UNIT).ifPresent(conf::setPenaltyUnit);
        parseOptionalAlgo(nodeElement,Configuration.RLConfig.ALGORITHM,Configuration.DEFAULT_ALGO).ifPresent(conf::setAlgoirthm);

        return conf;
    }

    private static Optional<Configuration.Algoirthm> parseOptionalAlgo(Element nodeElement, Configuration.RLConfig rlConfig, Configuration.Algoirthm defaultAlgo) {
        String value = getTagValue(nodeElement, rlConfig.getConfig());

        return (value != null && !value.isEmpty()) ? Optional.ofNullable(Configuration.Algoirthm.getAlgorithm(value)) : Optional.ofNullable(defaultAlgo);
    }

    private static String getTagValue(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent() : null;
    }


    private static String requireConfig(Element element, Configuration.RLConfig tag) {
        return Objects.requireNonNull(getTagValue(element, tag.getConfig()), tag.name() + " is required");
    }

    private static Integer parseRequiredInt(Element element, Configuration.RLConfig tag) {
        return Integer.parseInt(requireConfig(element, tag));
    }

    private static Optional<Integer> parseOptionalInt(Element element, Configuration.RLConfig tag, Integer defaultVal) {
        String value = getTagValue(element, tag.getConfig());
        return (value != null && !value.isEmpty()) ? Optional.of(Integer.parseInt(value)) : Optional.of(defaultVal);
    }


    private static Optional<TimeUnit> parseOptionalTimeUnit(Element element, Configuration.RLConfig tag, TimeUnit defaultVal){
        String value = getTagValue(element, tag.getConfig());

        if (value != null && !value.isEmpty()) {
            switch (value.toLowerCase()) {
                case "nanoseconds":
                    return Optional.of(TimeUnit.NANOSECONDS);
                case "microseconds":
                    return Optional.of(TimeUnit.MICROSECONDS);
                case "milliseconds":
                    return Optional.of(TimeUnit.MILLISECONDS);
                case "seconds":
                    return Optional.of(TimeUnit.SECONDS);
                case "minutes":
                    return Optional.of(TimeUnit.MINUTES);
                case "hours":
                    return Optional.of(TimeUnit.HOURS);
                case "days":
                    return Optional.of(TimeUnit.DAYS);
                default:
                    // Invalid unit, return empty or throw an exception
                    return Optional.of(defaultVal);
            }
        } else {
            return Optional.of(defaultVal);
        }

    }

    public static Configuration buildConfigurationFromJson(JSONObject jsonObject) throws JSONException {
        String uri = jsonObject.getString(Configuration.RLConfig.URI.getConfig());
        int limit = jsonObject.getInt(Configuration.RLConfig.LIMIT.getConfig());
        int window = jsonObject.getInt(Configuration.RLConfig.WINDOW.getConfig());

        Configuration conf = new Configuration(uri, limit, window);

        if (jsonObject.has(Configuration.RLConfig.BURST.getConfig())) {
            conf.setBurst(jsonObject.getInt(Configuration.RLConfig.BURST.getConfig()));
        }
        if (jsonObject.has(Configuration.RLConfig.TTL.getConfig())) {
            conf.setTtl(jsonObject.getInt(Configuration.RLConfig.TTL.getConfig()));
        }
        if (jsonObject.has(Configuration.RLConfig.PENALTY_TIME.getConfig())) {
            conf.setPenalty(jsonObject.getInt(Configuration.RLConfig.PENALTY_TIME.getConfig()));
        }
        if (jsonObject.has(Configuration.RLConfig.LIMIT_UNIT.getConfig())) {
            conf.setLimitUnit(TimeUnit.valueOf(jsonObject.getString(Configuration.RLConfig.LIMIT_UNIT.getConfig()).toUpperCase()));
        }
        if (jsonObject.has(Configuration.RLConfig.WINDOW_UNIT.getConfig())) {
            conf.setWindowUnit(TimeUnit.valueOf(jsonObject.getString(Configuration.RLConfig.WINDOW_UNIT.getConfig()).toUpperCase()));
        }
        if (jsonObject.has(Configuration.RLConfig.TTL_UNIT.getConfig()) ){
            conf.setTtlUnit(TimeUnit.valueOf(jsonObject.getString(Configuration.RLConfig.TTL_UNIT.getConfig()).toUpperCase()));
        }
        if (jsonObject.has(Configuration.RLConfig.PENALTY_TIME_UNIT.getConfig())) {
            conf.setPenaltyUnit(TimeUnit.valueOf(jsonObject.getString(Configuration.RLConfig.PENALTY_TIME_UNIT.getConfig()).toUpperCase()));
        }

        if (jsonObject.has(Configuration.RLConfig.ALGORITHM.getConfig())){
            conf.setAlgoirthm(Configuration.Algoirthm.getAlgorithm(jsonObject.getString(Configuration.RLConfig.ALGORITHM.getConfig())));
        }


        //Need to change every timeunit to seconds for easier purpose , can change to milli seconds for larger tech companies :)


        return conf;
    }



}
