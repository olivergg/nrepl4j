package io.olivergg.nrepl4j;

import clojure.java.api.Clojure;
import clojure.lang.Compiler;
import clojure.lang.IFn;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Embeds a Clojure nREPL server in a Java process for live runtime
 * inspection. nREPL has no authentication: anyone who can reach the port
 * gets arbitrary code execution as the JVM process. Always bind to a
 * loopback address ({@code 127.0.0.1} / {@code ::1}), never to a
 * wildcard address ({@code 0.0.0.0} / {@code ::} / {@code ::0}).
 */
public final class NReplServer implements AutoCloseable {

    private static final Set<String> WILDCARD_BINDS = Set.of("0.0.0.0", "::", "::0", "0:0:0:0:0:0:0:0");

    private final Closeable socketServer;
    private final int port;

    private NReplServer(Closeable socketServer, int port) {
        this.socketServer = socketServer;
        this.port = port;
    }

    public int port() {
        return port;
    }

    /** Starts an nREPL server using the given options, scanning for a free port if needed. */
    public static NReplServer start(NReplServerOptions options) {
        if (WILDCARD_BINDS.contains(options.bind())) {
            System.err.println("[NReplServer] WARNING: binding nREPL to " + options.bind()
                    + " exposes unauthenticated remote code execution on every network interface. "
                    + "Use 127.0.0.1 or ::1 unless you have your own network-level protection (firewall, VPN-only host...).");
        }

        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("nrepl.server"));

        int port = findAvailablePort(options.startPort(), options.endPort(), options.bind());

        IFn startServer = Clojure.var("nrepl.server", "start-server");
        IFn apply = Clojure.var("clojure.core", "apply");
        Object serverOptions = Clojure.read(String.format("[:port %d :bind \"%s\"]", port, options.bind()));
        Closeable socketServer = (Closeable) apply.invoke(startServer, serverOptions);

        options.bindings().forEach(NReplContext::put);
        for (String resource : options.classpathResourcesToLoad()) {
            loadClasspathResource(resource);
        }

        return new NReplServer(socketServer, port);
    }

    /** Compiles and loads a .clj file found on the classpath, making its vars available in the REPL. */
    public static void loadClasspathResource(String resourcePath) {
        try (var stream = NReplServer.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalArgumentException("Classpath resource not found: " + resourcePath);
            }
            Compiler.load(new PushbackReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load classpath resource: " + resourcePath, e);
        }
    }

    private static int findAvailablePort(int startPort, int endPort, String bind) {
        for (int port = startPort; port < endPort; port++) {
            if (isTcpPortAvailable(port, bind)) {
                return port;
            }
        }
        throw new IllegalStateException("No available port in range [" + startPort + ", " + endPort + ")");
    }

    private static boolean isTcpPortAvailable(int port, String bind) {
        try (ServerSocket socket = new ServerSocket()) {
            socket.bind(new InetSocketAddress(bind, port));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        socketServer.close();
    }
}
