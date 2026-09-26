package com.example.quarkusnrepldemo;

import io.olivergg.nrepl4j.NReplServer;
import io.olivergg.nrepl4j.NReplServerOptions;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.io.IOException;
import java.util.List;

/**
 * CDI.current() is a JVM-wide static accessor in a running Quarkus/Weld container,
 * so unlike Spring there's no context to publish - cdi.clj just works.
 */
@ApplicationScoped
public class NReplBootstrap {

    private NReplServer server;

    void onStart(@Observes StartupEvent event) {
        var options = NReplServerOptions.defaults()
                .withClasspathResourcesToLoad(List.of("clojure/helpers.clj", "clojure/cdi.clj"));
        this.server = NReplServer.start(options);
        System.out.println("nREPL server listening on port " + server.port());
    }

    void onStop(@Observes ShutdownEvent event) throws IOException {
        server.close();
    }

    public int port() {
        return server.port();
    }
}
