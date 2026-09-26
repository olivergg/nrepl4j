package com.example.quarkusnrepldemo;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class NReplSmokeTest {

    @Inject
    NReplBootstrap bootstrap;

    @Test
    void nreplAcceptsConnections() throws IOException {
        try (Socket socket = new Socket("127.0.0.1", bootstrap.port())) {
            assertTrue(socket.isConnected());
        }
    }

    @Test
    void cdiCljResolvesRealCdiBean() {
        IFn getBean = Clojure.var("io.olivergg.nrepl4j.cdi", "get-bean");
        GreetingService bean = (GreetingService) getBean.invoke(GreetingService.class);
        assertEquals("Hello, World!", bean.greet("World"));
    }
}
