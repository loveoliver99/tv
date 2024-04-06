package com.evangelsoft.econnect.util;

import java.util.concurrent.ConcurrentHashMap;

public class ApplicationGlobal {
    private static ConcurrentHashMap<String, Object> globalData = new ConcurrentHashMap<>();
    private static final ApplicationGlobal instance = new ApplicationGlobal();

    public ApplicationGlobal() {
    }
    
    public static ApplicationGlobal getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) globalData.get(key);
    }
     
    public <T> T get(String key, Class<T> type) {
        Object value = globalData.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        } else {
            // 可以记录日志或抛出异常
            return null;
        }
    }

    public static void set(String key, Object value) {
        globalData.put(key, value);
    }

    public static void remove(String key) {
        globalData.remove(key);
    }
}