package com.mrqtech.code_words.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DifficultyTest {

    @Test
    void getRandomLength_ReturnsValueInRange_ForEasy() {
        // Arrange
        Difficulty easy = Difficulty.EASY;
        int iterations = 10; // Run multiple times to account for randomness

        // Act & Assert
        for (int i = 0; i < iterations; i++) {
            int length = easy.getRandomLength();
            assertTrue(length >= 4 && length <= 6, "Random length should be between 4 and 6 for EASY, got: " + length);
        }
    }

    @Test
    void getRandomLength_ReturnsValueInRange_ForNormal() {
        // Arrange
        Difficulty normal = Difficulty.NORMAL;
        int iterations = 10;

        // Act & Assert
        for (int i = 0; i < iterations; i++) {
            int length = normal.getRandomLength();
            assertTrue(length >= 7 && length <= 9, "Random length should be between 7 and 9 for NORMAL, got: " + length);
        }
    }

    @Test
    void getRandomLength_ReturnsValueInRange_ForHard() {
        // Arrange
        Difficulty hard = Difficulty.HARD;
        int iterations = 10;

        // Act & Assert
        for (int i = 0; i < iterations; i++) {
            int length = hard.getRandomLength();
            assertTrue(length >= 10 && length <= 13, "Random length should be between 10 and 13 for HARD, got: " + length);
        }
    }
}