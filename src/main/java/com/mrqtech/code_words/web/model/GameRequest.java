package com.mrqtech.code_words.web.model;

import com.mrqtech.code_words.model.Difficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "Request payload to start or join a game")
public class GameRequest {

    @Schema(description = "Difficulty level of the game",
            example = "EASY",
            defaultValue = "EASY")
    private Difficulty difficulty = Difficulty.EASY;

    @Schema(description = "Username of the player", example = "playerOne")
    private String username;
}