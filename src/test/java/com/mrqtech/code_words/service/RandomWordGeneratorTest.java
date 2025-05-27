package com.mrqtech.code_words.service;

import com.mrqtech.code_words.model.Difficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RandomWordGeneratorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RandomWordGenerator randomWordGenerator;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize wordMap with test CSV
        randomWordGenerator.init();
    }

    @Test
    void init_LoadsWordsFromCsv_Successfully() {
        // Act (init already called in setUp)
        // Assert
        Map<Integer, List<String>> wordMap = (Map<Integer, List<String>>) ReflectionTestUtils.getField(randomWordGenerator, "wordMap");
        assertNotNull(wordMap, "wordMap should not be null");
        assertEquals(3, wordMap.size(), "wordMap should have entries for 3 lengths");
        assertEquals(List.of("apple", "grape"), wordMap.get(5), "Words of length 5 should be loaded");
        assertEquals(List.of("banana"), wordMap.get(6), "Words of length 6 should be loaded");
        assertEquals(List.of("avocado"), wordMap.get(7), "Words of length 7 should be loaded");
    }

    @Test
    void getRandomWord_ReturnsWordFromApi_WhenApiCallSucceeds() {
        // Arrange
        Difficulty difficulty = mock(Difficulty.class);
        when(difficulty.getRandomLength()).thenReturn(5);
        when(restTemplate.getForObject(anyString(), eq(String[].class))).thenReturn(new String[]{"hello"});

        // Act
        String result = randomWordGenerator.getRandomWord(difficulty);

        // Assert
        verify(restTemplate).getForObject("https://random-word-api.herokuapp.com/word?length=5", String[].class);
        assertEquals("hello", result, "Should return word from API");
    }

    @Test
    void getRandomWord_FallsBackToLocalWord_WhenApiReturnsEmpty() {
        // Arrange
        Difficulty difficulty = mock(Difficulty.class);
        when(difficulty.getRandomLength()).thenReturn(5);
        when(restTemplate.getForObject(anyString(), eq(String[].class))).thenReturn(new String[]{});

        // Act
        String result = randomWordGenerator.getRandomWord(difficulty);

        // Assert
        verify(restTemplate).getForObject("https://random-word-api.herokuapp.com/word?length=5", String[].class);
        assertTrue(List.of("apple", "grape").contains(result), "Should return a word from local wordMap");
    }

    @Test
    void getRandomWord_FallsBackToLocalWord_WhenApiThrowsException() {
        // Arrange
        Difficulty difficulty = mock(Difficulty.class);
        when(difficulty.getRandomLength()).thenReturn(5);
        when(restTemplate.getForObject(anyString(), eq(String[].class))).thenThrow(new RestClientException("API error"));

        // Act
        String result = randomWordGenerator.getRandomWord(difficulty);

        // Assert
        verify(restTemplate).getForObject("https://random-word-api.herokuapp.com/word?length=5", String[].class);
        assertTrue(List.of("apple", "grape").contains(result), "Should return a word from local wordMap");
    }

    @Test
    void getRandomWord_ThrowsException_WhenNoWordsAvailable() {
        // Arrange
        Difficulty difficulty = mock(Difficulty.class);
        when(difficulty.getRandomLength()).thenReturn(8); // No words for length 8 in CSV
        when(restTemplate.getForObject(anyString(), eq(String[].class))).thenReturn(new String[]{});

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> randomWordGenerator.getRandomWord(difficulty));
        assertEquals("Can't generate word, no words found with length : 8", exception.getMessage());
    }
}