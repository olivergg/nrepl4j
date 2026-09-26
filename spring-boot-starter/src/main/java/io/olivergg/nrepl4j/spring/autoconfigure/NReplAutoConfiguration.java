package io.olivergg.nrepl4j.spring.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-starts an embedded Clojure nREPL server in any Spring Boot app that has this
 * starter on its classpath. Disable with {@code clojure.nrepl.enabled=false}.
 */
@AutoConfiguration
@EnableConfigurationProperties(NReplProperties.class)
@ConditionalOnProperty(prefix = "clojure.nrepl", name = "enabled", matchIfMissing = true)
public class NReplAutoConfiguration {

    @Bean
    NReplLifecycle nReplLifecycle(NReplProperties properties) {
        return new NReplLifecycle(properties);
    }
}
