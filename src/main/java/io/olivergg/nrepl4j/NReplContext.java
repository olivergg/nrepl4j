package io.olivergg.nrepl4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Process-wide registry an app publishes objects into (e.g. its ApplicationContext)
 * so that classpath-loaded .clj helpers (spring.clj, cdi.clj...) can read them back,
 * since there's no portable way to auto-discover them (e.g. embedded Spring Boot has
 * no static context holder the way a servlet-deployed WAR does).
 */
public final class NReplContext {

    private static final Map<String, Object> BINDINGS = new ConcurrentHashMap<>();

    private NReplContext() {
    }

    public static void put(String key, Object value) {
        BINDINGS.put(key, value);
    }

    public static Object get(String key) {
        return BINDINGS.get(key);
    }
}
