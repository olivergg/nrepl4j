package com.example.quarkusnrepldemo;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

/** Only reached via a dynamic CDI.current().select(...) lookup (nrepl helpers), so
 *  Quarkus's build-time unused-bean removal would otherwise strip it. */
@Unremovable
@ApplicationScoped
public class GreetingService {

    private final String prefix = "Hello, ";

    public String greet(String name) {
        return prefix + name + "!";
    }
}
