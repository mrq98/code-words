package com.mrqtech.code_words.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.stream.Collectors;

@Data
@Schema(description = "Response object representing the current state of a game")
public class Game {

    @Schema(description = "Unique identifier of the game", example = "1")
    private Long id;

    @Schema(description = "Masked version of the word to guess", example = "_ A _ A _")
    private String maskedWord;

    @Schema(description = "Number of remaining incorrect attempts allowed", example = "5")
    private Integer remainingAttempts;

    @Schema(description = "Current status of the game", example = "IN_PROGRESS")
    private Status status;

    @Schema(description = "The full word to guess; visible only when the game is finished (status is not IN_PROGRESS)",
            example = "BANANA",
            nullable = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String word;

    public void setMaskedWord(String maskedWord) {
        this.maskedWord =  maskedWord.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining(" "));
    }
}
