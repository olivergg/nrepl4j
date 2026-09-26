package com.example.springnrepldemo;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** No bootstrap code in this app - nrepl4j-spring-boot-starter auto-starts from its mere presence on the classpath. */
@SpringBootTest
class NReplSmokeTest {

    @Test
    void springCljResolvesRealSpringBean() {
        IFn getBean = Clojure.var("io.olivergg.nrepl4j.spring", "get-bean");
        GreetingService bean = (GreetingService) getBean.invoke("greetingService");
        assertEquals("Hello, World!", bean.greet("World"));
    }
}
