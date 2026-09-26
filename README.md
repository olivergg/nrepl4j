# 🔌 nrepl4j

Embeds a Clojure nREPL server in a Java process. Clojure is just a jar — this works in **any** JVM app (plain `main`, Spring Boot, Quarkus, legacy WAR...); Spring/CDI bean lookup are optional add-ons, not a requirement.

Connect to it and you get live access to the running app: inspect any object, patch private state, call any method, hot-fix a bug — no redeploy.

## 🔒 Security

nREPL has **no authentication** — anyone who can reach the port gets arbitrary code execution as your JVM process.

| Bind to | Safe? | Why |
|---|---|---|
| `127.0.0.1` / `::1` | ✅ | loopback only, same machine |
| `0.0.0.0` / `::` / `::0` | ❌ | **every** network interface, IPv4 and IPv6 wildcards alike |

`NReplServerOptions.defaults()` already binds to `127.0.0.1`. `NReplServer.start` logs a warning to stderr if you override it to a wildcard bind — don't unless you have your own network-level protection (firewall, VPN-only host, SSH tunnel...).

## 📦 Install

```xml
<dependency>
  <groupId>io.github.olivergg</groupId>
  <artifactId>nrepl4j</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Requires **Java 17+**. Ships Clojure `1.12.6` + nREPL `1.7.0`.

## 🚀 Quick start

```java
try (NReplServer server = NReplServer.start(NReplServerOptions.defaults())) {
    System.out.println("nREPL on port " + server.port());
}
```

## ⚙️ Options

| Field | Default | Purpose |
|---|---|---|
| `startPort` / `endPort` | `5555` / `6666` | port scan range |
| `bind` | `127.0.0.1` | interface to bind |
| `classpathResourcesToLoad` | `[]` | `.clj` files to load at startup |
| `bindings` | `{}` | objects published to [`NReplContext`](#-nreplcontext) before those files load |

```java
NReplServerOptions.defaults()
    .withClasspathResourcesToLoad(List.of("clojure/helpers.clj"))
    .withBindings(Map.of("applicationContext", ctx));
```

## 🧰 Bundled helpers (`clojure/*.clj`)

| File | Load when | Provides |
|---|---|---|
| `helpers.clj` | always | `private-field`, `get-field-val`, `set-field-val!`, `call-method`, `unproxy`, `to-map`, `thread-dump`, `clearns` |
| `spring.clj` | Spring on classpath | `get-bean` via `NReplContext` (needs `applicationContext` binding) |
| `cdi.clj` | CDI container (Weld/Quarkus) | `get-bean` via `CDI.current()` (no binding needed) |

## 🗄️ NReplContext

Static registry an app publishes objects into so classpath-loaded helpers can read them back — needed for Spring (embedded Boot has no static context holder), not for CDI (`CDI.current()` is already global).

```java
NReplContext.put("applicationContext", applicationContext); // or via .withBindings(...)
```

## 🌱 Spring Boot starter (zero code)

For Spring Boot, skip all of the above — add the starter and it auto-configures itself:

```xml
<dependency>
  <groupId>io.github.olivergg</groupId>
  <artifactId>nrepl4j-spring-boot-starter</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

| Property (`clojure.nrepl.*`) | Default | Purpose |
|---|---|---|
| `enabled` | `true` | set `false` to disable entirely |
| `start-port` / `end-port` | `5555` / `6666` | port scan range |
| `bind` | `127.0.0.1` | interface to bind |
| `extra-resources` | `[]` | extra `.clj` files, loaded after `helpers.clj` + `spring.clj` |

## 🖥️ Framework integration

Any framework works — these are just the ones with a bean-lookup helper included:

| Framework | Hook | Binding needed? |
|---|---|---|
| Plain Java | anywhere in `main` | ❌ |
| Spring Boot | [starter](#-spring-boot-starter-zero-code) above, or `ApplicationContextAware` + `@PreDestroy` | ✅ `applicationContext` |
| Quarkus / CDI | `@Observes StartupEvent` / `ShutdownEvent` | ❌ |

Full working examples: [`examples/spring-boot-nrepl-demo`](examples/spring-boot-nrepl-demo) (using the starter) and [`examples/quarkus-nrepl-demo`](examples/quarkus-nrepl-demo).

## 🧪 Test

```bash
mvn test
```

## 📄 License

[MIT](LICENSE)
