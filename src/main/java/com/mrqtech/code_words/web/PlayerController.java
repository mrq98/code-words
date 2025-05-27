package com.mrqtech.code_words.web;
import com.mrqtech.code_words.model.Player;
import com.mrqtech.code_words.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Player", description = "Operations related to players and leaderboard")
@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @Operation(summary = "Get player by username",
            description = "Retrieve a player profile by their username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Player found",
                            content = @Content(schema = @Schema(implementation = Player.class))),
                    @ApiResponse(responseCode = "404", description = "Player not found")
            })
    @GetMapping
    public ResponseEntity<Player> getPlayer(
            @Parameter(description = "Username of the player to retrieve", required = true)
            @RequestParam String username) {
        Player player = playerService.getPlayerByUsername(username);
        return ResponseEntity.ok(player);
    }

    @Operation(summary = "Get leaderboard",
            description = "Retrieve a list of top players sorted by score, limited by the given number",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Leaderboard retrieved",
                            content = @Content(schema = @Schema(implementation = Player.class, type = "array")))
            })
    @GetMapping("/leaderboard")
    public ResponseEntity<List<Player>> getLeaderBoard(
            @Parameter(description = "Number of top players to return", required = true, example = "10")
            @RequestParam int limit) {
        List<Player> leaderBoard = playerService.getLeaderBoard(limit);
        return ResponseEntity.ok(leaderBoard);
    }
}
