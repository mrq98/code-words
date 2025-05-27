package com.mrqtech.code_words.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CodeWordsConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
