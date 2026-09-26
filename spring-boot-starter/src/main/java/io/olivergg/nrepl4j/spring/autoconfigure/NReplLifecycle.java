package io.olivergg.nrepl4j.spring.autoconfigure;

import io.olivergg.nrepl4j.NReplServer;
import io.olivergg.nrepl4j.NReplServerOptions;
import jakarta.annotation.PreDestroy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/** Starts the embedded nREPL server once the ApplicationContext is available, and stops it on shutdown. */
class NReplLifecycle implements ApplicationContextAware {

    private final NReplProperties properties;
    private NReplServer server;

    NReplLifecycle(NReplProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        List<String> resources = Stream.concat(
                Stream.of("clojure/helpers.clj", "clojure/spring.clj"),
                properties.getExtraResources().stream()).toList();

        var options = new NReplServerOptions(
                properties.getStartPort(), properties.getEndPort(), properties.getBind(),
                resources, Map.of("applicationContext", applicationContext));

        this.server = NReplServer.start(options);
    }

    @PreDestroy
    void stop() throws IOException {
        if (server != null) {
            server.close();
        }
    }

    int port() {
        return server.port();
    }
}
