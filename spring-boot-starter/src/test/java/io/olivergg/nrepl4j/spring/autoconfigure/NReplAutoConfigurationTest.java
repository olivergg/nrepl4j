package io.olivergg.nrepl4j.spring.autoconfigure;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** No bootstrap code here - the starter wires everything from the dependency alone. */
@SpringBootTest(classes = NReplAutoConfigurationTest.TestApp.class)
class NReplAutoConfigurationTest {

    @Autowired
    private NReplLifecycle lifecycle;

    @SpringBootApplication
    static class TestApp {
        @Bean
        Greeter greeter() {
            return new Greeter();
        }
    }

    @Component
    static class Greeter {
        public String greet(String name) {
            return "Hello, " + name + "!";
        }
    }

    @Test
    void autoStartsAndResolvesBeansThroughSpringClj() throws IOException {
        IFn getBean = Clojure.var("io.olivergg.nrepl4j.spring", "get-bean");
        Greeter bean = (Greeter) getBean.invoke("greeter");
        assertEquals("Hello, World!", bean.greet("World"));
    }

    @Test
    void nreplAcceptsConnections() throws IOException {
        try (Socket socket = new Socket("127.0.0.1", lifecycle.port())) {
            assertTrue(socket.isConnected());
        }
    }
}
