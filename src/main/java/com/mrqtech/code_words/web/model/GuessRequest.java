package com.mrqtech.code_words.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload containing a guess, which can be a letter or a word")
public class GuessRequest {

    @Schema(description = "The guess submitted by the player; can be a single letter or a full word",
            example = "e")
    private String guess;
}
