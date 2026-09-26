package io.olivergg.nrepl4j;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NReplServerTest {

    @Test
    void startsAndAcceptsConnections() throws IOException {
        try (NReplServer server = NReplServer.start(NReplServerOptions.defaults())) {
            try (Socket socket = new Socket("127.0.0.1", server.port())) {
                assertTrue(socket.isConnected());
            }
        }
    }

    @Test
    void loadsHelpersAndExposesUsableFunctions() throws IOException {
        var options = NReplServerOptions.defaults().withClasspathResourcesToLoad(List.of("clojure/helpers.clj"));
        try (NReplServer server = NReplServer.start(options)) {
            IFn getFieldVal = Clojure.var("io.olivergg.nrepl4j.helpers", "get-field-val");
            Object port = getFieldVal.invoke(server, "port");
            assertEquals(server.port(), port);

            IFn callMethod = Clojure.var("io.olivergg.nrepl4j.helpers", "call-method");
            Object toString = callMethod.invoke(server, "toString");
            assertTrue(((String) toString).contains("NReplServer"));

            IFn threadDump = Clojure.var("io.olivergg.nrepl4j.helpers", "thread-dump");
            Object dump = threadDump.invoke();
            assertTrue(((String) dump).contains("main"));
        }
    }
}
