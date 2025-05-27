package com.mrqtech.code_words.web;

import com.mrqtech.code_words.model.Game;
import com.mrqtech.code_words.service.GameService;
import com.mrqtech.code_words.web.model.ForfeitRequest;
import com.mrqtech.code_words.web.model.GameRequest;
import com.mrqtech.code_words.web.model.GuessRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Game", description = "Operations related to game management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @Operation(
            summary = "Start a new game",
            description = "Creates a new game with the specified difficulty and username (optional). If no payload is provided, defaults are used.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game started successfully",
                            content = @Content(schema = @Schema(implementation = Game.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @PostMapping
    public ResponseEntity<Game> startNewGame(
            @Parameter(description = "Optional game start request payload. If omitted, defaults are used.", required = false)
            @RequestBody(required = false) GameRequest gameRequest) {

        if (gameRequest == null) {
            gameRequest = new GameRequest(); // defaults set in the class (e.g., difficulty=EASY)
        }

        Game game = gameService.createNewGame(gameRequest);
        return ResponseEntity.ok(game);
    }

    @Operation(summary = "Make a guess in an existing game",
            description = "Submits a guess (letter or word) for a game by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Guess processed successfully",
                            content = @Content(schema = @Schema(implementation = Game.class))),
                    @ApiResponse(responseCode = "404", description = "Game not found"),
                    @ApiResponse(responseCode = "400", description = "Game is already finished")
            })
    @PostMapping("/{id}/guess")
    public ResponseEntity<Game> makeAGuess(
            @Parameter(description = "ID of the game", required = true)
            @PathVariable Long id,

            @Parameter(description = "Guess payload containing a letter or word", required = true)
            @RequestBody GuessRequest guess) {
        guess.setGameId(id);
        Game game = gameService.processGuess(guess);
        return ResponseEntity.ok(game);
    }


    @Operation(summary = "Forfeit the game.",
            description = "Submit gameId to forfeit to reveal the word.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Forfeit processed successfully",
                            content = @Content(schema = @Schema(implementation = Game.class))),
                    @ApiResponse(responseCode = "404", description = "Game not found"),
                    @ApiResponse(responseCode = "400", description = "Game is already finished")
            })
    @PostMapping({"{id}/forfeit"})
    public ResponseEntity<Game> forfeitGame(
            @Parameter(description = "ID of the game", required = true)
            @PathVariable Long id,
            @Parameter(description = "Optional forfeit game request payload. Only for game with username", required = false)
            @RequestBody(required = false) ForfeitRequest request) {
        Game game = null;
        if (request == null) {
            game = gameService.forfeitGame(new ForfeitRequest(1L, null));
        } else {
            request.setGameId(id);
            game = gameService.forfeitGame(request);
        }
        return ResponseEntity.ok(game);
    }


    @Operation(summary = "Get game by ID",
            description = "Retrieves the current state of the game by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Game.class))),
                    @ApiResponse(responseCode = "404", description = "Game not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(
            @Parameter(description = "ID of the game", required = true)
            @PathVariable Long id) {
        Game game = gameService.getGameById(id);
        return ResponseEntity.ok(game);
    }
}