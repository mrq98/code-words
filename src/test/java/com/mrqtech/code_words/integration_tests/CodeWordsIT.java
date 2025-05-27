package com.mrqtech.code_words.integration_tests;

import com.intuit.karate.junit5.Karate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CodeWordsIT {

    @LocalServerPort
    private int port;

    @Karate.Test
    Karate testAllFeatures() {
        return Karate.run("classpath:karate")
                .systemProperty("local.server.port", String.valueOf(port));
    }
}
