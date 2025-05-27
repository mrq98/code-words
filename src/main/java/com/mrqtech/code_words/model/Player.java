package com.mrqtech.code_words.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Player model representing a game player and their scores")
public class Player {

    @Schema(description = "Unique identifier of the player", example = "123")
    private Long id;

    @Schema(description = "Username of the player", example = "playerOne")
    private String username;

    @Schema(description = "Total score accumulated by the player", example = "2500")
    private Integer totalScore;

    @Schema(description = "Total number of easy puzzles solved by the player", example = "15")
    private Integer totalEasySolved;

    @Schema(description = "Total number of normal puzzles solved by the player", example = "10")
    private Integer totalNormalSolved;

    @Schema(description = "Total number of hard puzzles solved by the player", example = "5")
    private Integer totalHardSolved;
}
