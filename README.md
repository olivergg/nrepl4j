# nrepl4j

[![Maven Central](https://img.shields.io/maven-central/v/io.github.olivergg/nrepl4j?label=nrepl4j)](https://central.sonatype.com/artifact/io.github.olivergg/nrepl4j)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.olivergg/nrepl4j-spring-boot-starter?label=spring-boot-starter)](https://central.sonatype.com/artifact/io.github.olivergg/nrepl4j-spring-boot-starter)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Embed a Clojure nREPL server in a Java process.

Clojure is just a jar, so this works in any JVM app: plain `main`, Spring Boot, Quarkus, an old WAR. Spring and CDI bean lookup are optional extras.

Once connected from your editor, you can poke at the running app: inspect objects, read or patch private state, call methods, hot-fix a bug without redeploying.

## Security

> [!CAUTION]
> Running an nREPL in production means a remote shell into your live app. Anyone who gets to it can read data, change state, or take the process down. If you enable it in prod, keep it on loopback, restrict who can reach the host, and consider turning it off (`clojure.nrepl.enabled=false`) when you don't need it.

nREPL has no authentication. Anyone who can reach the port can run arbitrary code in your JVM.

The default bind is `127.0.0.1`, which is what you want. `NReplServer.start` prints a warning to stderr if you bind to a wildcard address (`0.0.0.0`, `::`). Only do that if something else protects the port (firewall, VPN-only host, SSH tunnel).

## Install

```xml
<repositories>
  <!-- nrepl:nrepl is only published on Clojars, and Central doesn't allow
       <repositories> in published POMs, so you need to add it yourself. -->
  <repository>
    <id>clojars</id>
    <url>https://repo.clojars.org/</url>
  </repository>
</repositories>

<dependency>
  <groupId>io.github.olivergg</groupId>
  <artifactId>nrepl4j</artifactId>
  <version>1.0.0</version>
</dependency>
```

Java 17+. Ships with Clojure 1.12.6 and nREPL 1.7.0.

## Usage

```java
try (NReplServer server = NReplServer.start(NReplServerOptions.defaults())) {
    System.out.println("nREPL on port " + server.port());
}
```

### Options

| Field | Default | |
|---|---|---|
| `startPort` / `endPort` | `5555` / `6666` | port scan range |
| `bind` | `127.0.0.1` | interface to bind |
| `classpathResourcesToLoad` | `[]` | `.clj` files loaded at startup |
| `bindings` | `{}` | objects put in [`NReplContext`](#nreplcontext) before those files load |

```java
NReplServerOptions.defaults()
    .withClasspathResourcesToLoad(List.of("clojure/helpers.clj"))
    .withBindings(Map.of("applicationContext", ctx));
```

### Bundled helpers

| File | When | Provides |
|---|---|---|
| `clojure/helpers.clj` | always | `private-field`, `get-field-val`, `set-field-val!`, `call-method`, `unproxy`, `to-map`, `thread-dump`, `clearns` |
| `clojure/spring.clj` | Spring | `get-bean`, needs an `applicationContext` binding |
| `clojure/cdi.clj` | CDI (Weld, Quarkus) | `get-bean` via `CDI.current()` |

### NReplContext

A static map the app can publish objects into, so `.clj` helpers can find them. Spring needs it (embedded Boot has no static context holder); CDI doesn't, `CDI.current()` is already global.

```java
NReplContext.put("applicationContext", applicationContext); // or .withBindings(...)
```

## Spring Boot starter

With Spring Boot you can skip the above and just add the starter:

```xml
<dependency>
  <groupId>io.github.olivergg</groupId>
  <artifactId>nrepl4j-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

| Property (`clojure.nrepl.*`) | Default | |
|---|---|---|
| `enabled` | `true` | |
| `start-port` / `end-port` | `5555` / `6666` | port scan range |
| `bind` | `127.0.0.1` | interface to bind |
| `extra-resources` | `[]` | extra `.clj` files, loaded after `helpers.clj` and `spring.clj` |

## Other frameworks

- Plain Java: start the server anywhere in `main`.
- Spring without the starter: `ApplicationContextAware` + `@PreDestroy`, and pass the `applicationContext` binding.
- Quarkus / CDI: `@Observes StartupEvent` / `ShutdownEvent`, no binding needed.

Working examples: [`examples/spring-boot-nrepl-demo`](examples/spring-boot-nrepl-demo) (starter) and [`examples/quarkus-nrepl-demo`](examples/quarkus-nrepl-demo).

## Build

```bash
mvn test
```

## Credits

nrepl4j is a thin wrapper; the real work is done by:

- [Clojure](https://clojure.org/), by Rich Hickey and the Clojure core team
- [nREPL](https://nrepl.org/), created by Chas Emerick, maintained by Bozhidar Batsov and contributors
- [Clojars](https://clojars.org/), which hosts nREPL and much of the Clojure ecosystem
- [CIDER](https://cider.mx/), [Calva](https://calva.io/), [Cursive](https://cursive-ide.com/) and the other nREPL clients that make connecting to this useful

## License

[MIT](LICENSE)
