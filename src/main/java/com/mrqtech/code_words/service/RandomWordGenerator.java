package com.mrqtech.code_words.service;

import com.mrqtech.code_words.model.Difficulty;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Component
public class RandomWordGenerator {

    private final Map<Integer, List<String>> wordMap = new HashMap<>();
    private final RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/words.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String line = reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int length = Integer.parseInt(parts[0].trim());
                String word = parts[1].trim();

                wordMap.computeIfAbsent(length, k -> new ArrayList<>()).add(word);
            }

            log.info("Loaded {} words into memory.", wordMap.values().stream().mapToInt(List::size).sum());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load words from CSV", e);
        }
    }

    public String getRandomWord(Difficulty difficulty) {
        int length = difficulty.getRandomLength();
        try {
            String url = String.format("https://random-word-api.herokuapp.com/word?length=%s", length);
            String[] response = restTemplate.getForObject(url, String[].class);

            // the api returns a list, get only the first one
            if (response != null && response.length > 0) {
                return response[0];
            }

            // no valid result
            throw new RestClientException("No word generated from API");
        } catch (RestClientException e) {
            // fall back --  get from local word bank
            List<String> words = wordMap.get(length);

            // shouldn't happen unless no words are loaded from csv
            if (CollectionUtils.isEmpty(words)) {
                throw new IllegalStateException("Can't generate word, no words found with length : " + length);
            }

            return words.get(ThreadLocalRandom.current().nextInt(words.size()));
        }
    }

}