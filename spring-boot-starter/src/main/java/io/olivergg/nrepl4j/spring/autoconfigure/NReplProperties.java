package io.olivergg.nrepl4j.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "clojure.nrepl")
public class NReplProperties {

    /** Whether to start the embedded nREPL server. */
    private boolean enabled = true;

    /** First port to try. */
    private int startPort = 5555;

    /** Exclusive upper bound for the port scan. */
    private int endPort = 6666;

    /** Interface to bind to - never a wildcard address in a reachable environment. */
    private String bind = "127.0.0.1";

    /** Extra classpath .clj resources to load, in addition to helpers.clj and spring.clj. */
    private List<String> extraResources = List.of();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getStartPort() {
        return startPort;
    }

    public void setStartPort(int startPort) {
        this.startPort = startPort;
    }

    public int getEndPort() {
        return endPort;
    }

    public void setEndPort(int endPort) {
        this.endPort = endPort;
    }

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public List<String> getExtraResources() {
        return extraResources;
    }

    public void setExtraResources(List<String> extraResources) {
        this.extraResources = extraResources;
    }
}
