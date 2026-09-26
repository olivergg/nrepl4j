package io.olivergg.nrepl4j;

import java.util.List;
import java.util.Map;

/**
 * @param startPort first port to try
 * @param endPort exclusive upper bound for the port scan
 * @param bind interface to bind to; use "127.0.0.1" outside of trusted/local environments
 * @param classpathResourcesToLoad .clj files on the classpath to load into the REPL once started
 * @param bindings objects published into {@link NReplContext} before those resources load,
 *                  e.g. {@code Map.of("applicationContext", ctx)} for spring.clj's get-bean
 */
public record NReplServerOptions(
        int startPort, int endPort, String bind, List<String> classpathResourcesToLoad, Map<String, Object> bindings) {

    public static NReplServerOptions defaults() {
        return new NReplServerOptions(5555, 6666, "127.0.0.1", List.of(), Map.of());
    }

    public NReplServerOptions withClasspathResourcesToLoad(List<String> resources) {
        return new NReplServerOptions(startPort, endPort, bind, resources, bindings);
    }

    public NReplServerOptions withBindings(Map<String, Object> bindings) {
        return new NReplServerOptions(startPort, endPort, bind, classpathResourcesToLoad, bindings);
    }
}
