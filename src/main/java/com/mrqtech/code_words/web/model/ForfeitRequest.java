package com.mrqtech.code_words.web.model;



import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Request to forfeit a game")
public class ForfeitRequest {

    @Hidden
    private Long gameId;

    @Schema(
            description = "Username of the player forfeiting the game (optional)",
            example = "username - optional"
    )
    private String username;
}
