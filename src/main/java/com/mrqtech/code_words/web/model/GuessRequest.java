package com.mrqtech.code_words.web.model;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "Request payload containing a guess, which can be a letter or a word")
@AllArgsConstructor
public class GuessRequest {

    @Hidden
    private Long gameId;

    @Schema(description = "The guess submitted by the player; can be a single letter or a full word",
            example = "e")
    private String guess;

    @Schema(description = "Username of the guesser; required for game created with username", example = "playerOne")
    private String username;
}
