package com.mrqtech.code_words.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;


@Schema(description = "Difficulty level of the game")
public enum Difficulty {

    @Schema(description = "Easy difficulty (word length: 4–6, 6 attempts)")
    EASY(4, 6, 1, 6),

    @Schema(description = "Normal difficulty (word length: 7–9, 7 attempts)")
    NORMAL(7, 9, 2, 7),

    @Schema(description = "Hard difficulty (word length: 10–13, 8 attempts)")
    HARD(10, 13, 3,8);

    private final int minLength;
    private final int maxLength;

    @Getter
    private final int baseScore;

    @Getter
    private final int totalAttempts;

    Difficulty(int minLength, int maxLength, int baseScore, int totalAttempts) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.baseScore = baseScore;
        this.totalAttempts = totalAttempts;
    }

    public int getRandomLength() {
        return ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
    }

}