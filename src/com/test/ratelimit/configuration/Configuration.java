package com.test.ratelimit.configuration;

import java.util.concurrent.TimeUnit;

public class Configuration {

    public static final TimeUnit DEFAULT_TIME_UNIT= TimeUnit.SECONDS;
    public static final Integer DEFAULT_LIMIT = 10;
    public static final Integer DEFAULT_TTL = 90;
    public static final Integer DEFAULT_WINDOW = 1;
    public static final Integer DEFAULT_BURST = -1;
    public static final Algoirthm DEFAULT_ALGO = Algoirthm.LEAKY_BUCKET;
    public enum Algoirthm {
        LEAKY_BUCKET("leaky_bucket"),TOKEN_BUCKET("token_bucket"),FIXED_WINDOW("fixed_window"),MOVING_WINDOW_EXPIRATION("moving_window_expiration");


        private final String algorithm;
        Algoirthm(String algorithm) {
            this.algorithm=algorithm;
        }
        public String getAlgorithm(){
            return this.algorithm;
        }
        public static Algoirthm getAlgorithm(String algorithm){
            for (Algoirthm algo : Algoirthm.values()){
                if (algo.getAlgorithm().equals(algorithm)){
                    return algo;
                }
            }
            return null;
        }


    }

    public enum RLConfig{
        URI("uri"),LIMIT("limit"),LIMIT_UNIT("limitunit"),BURST("burst"),TTL("TTL"),TTL_UNIT("TTLunit"),
        PENALTY_TIME("penaltytime"),PENALTY_TIME_UNIT("penaltytimeunit"),WINDOW("interval"),WINDOW_UNIT("intervalunit"),ALGORITHM("algorithm");
        private final String config;

        RLConfig(String config) {
            this.config = config;
        }

        public String getConfig() {
            return config;
        }
    }

    String uri;
    Integer limit;
    TimeUnit limitUnit;
    Integer burst;
    Integer window;
    TimeUnit windowUnit;
    Integer ttl;
    TimeUnit ttlUnit;
    Integer penalty;
    TimeUnit penaltyUnit;
    Algoirthm algoirthm;

    public Algoirthm getAlgoirthm() {
        return algoirthm;
    }

    public void setAlgoirthm(Algoirthm algoirthm) {
        this.algoirthm = algoirthm;
    }

    public Configuration(String uri, Integer limit, Integer burst, Integer window) {
        this.uri = uri;
        this.limit = limit;
        this.burst = burst;
        this.window = window;

        this.limitUnit = DEFAULT_TIME_UNIT;
        this.windowUnit = DEFAULT_TIME_UNIT;
    }

    public Configuration(String uri, Integer limit, Integer window) {
        this.uri = uri;
        this.limit = limit;
        this.window = window;
        this.burst = DEFAULT_BURST;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public TimeUnit getLimitUnit() {
        return limitUnit;
    }

    public void setLimitUnit(TimeUnit limitUnit) {
        this.limitUnit = limitUnit;
    }

    public Integer getBurst() {
        return burst;
    }

    public void setBurst(Integer burst) {
        this.burst = burst;
    }

    public Integer getWindow() {
        return window;
    }

    public void setWindow(Integer window) {
        this.window = window;
    }

    public TimeUnit getWindowUnit() {
        return windowUnit;
    }

    public void setWindowUnit(TimeUnit windowUnit) {
        this.windowUnit = windowUnit;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public TimeUnit getTtlUnit() {
        return ttlUnit;
    }

    public void setTtlUnit(TimeUnit ttlUnit) {
        this.ttlUnit = ttlUnit;
    }

    public Integer getPenalty() {
        return penalty;
    }

    public void setPenalty(Integer penalty) {
        this.penalty = penalty;
    }

    public TimeUnit getPenaltyUnit() {
        return penaltyUnit;
    }

    public void setPenaltyUnit(TimeUnit penaltyUnit) {
        this.penaltyUnit = penaltyUnit;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "uri='" + uri + '\'' +
                ", limit=" + limit +
                ", limitUnit=" + limitUnit +
                ", burst=" + burst +
                ", window=" + window +
                ", windowUnit=" + windowUnit +
                ", ttl=" + ttl +
                ", ttlUnit=" + ttlUnit +
                ", penalty=" + penalty +
                ", penaltyUnit=" + penaltyUnit +
                '}';
    }
}
