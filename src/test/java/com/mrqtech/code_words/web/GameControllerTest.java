package com.mrqtech.code_words.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrqtech.code_words.model.Game;
import com.mrqtech.code_words.model.Status;
import com.mrqtech.code_words.service.GameService;
import com.mrqtech.code_words.web.model.ForfeitRequest;
import com.mrqtech.code_words.web.model.GameRequest;
import com.mrqtech.code_words.web.model.GuessRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    private Game sampleGame() {
        Game game = new Game();
        game.setId(1L);
        game.setMaskedWord("_ _ _ _ _");
        game.setRemainingAttempts(5);
        game.setStatus(Status.IN_PROGRESS);
        return game;
    }

    @Test
    void startNewGame_shouldReturnGame() throws Exception {
        Game game = sampleGame();
        Mockito.when(gameService.createNewGame(any())).thenReturn(game);

        GameRequest request = new GameRequest(); // will use default difficulty

        mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(game.getId()))
                .andExpect(jsonPath("$.maskedWord").exists());
    }

    @Test
    void makeAGuess_shouldReturnUpdatedGame() throws Exception {
        Game game = sampleGame();
        Mockito.when(gameService.processGuess(any(GuessRequest.class))).thenReturn(game);

        GuessRequest guessRequest = new GuessRequest(1L, "A", null);

        mockMvc.perform(post("/game/1/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guessRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(game.getId()));
    }

    @Test
    void forfeitGame_shouldReturnGameWithStatusLost() throws Exception {
        Game game = sampleGame();
        game.setStatus(Status.LOST);
        Mockito.when(gameService.forfeitGame(new ForfeitRequest(1L,null))).thenReturn(game);

        mockMvc.perform(post("/game/1/forfeit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LOST"));
    }

    @Test
    void getGameById_shouldReturnGame() throws Exception {
        Game game = sampleGame();
        Mockito.when(gameService.getGameById(1L)).thenReturn(game);

        mockMvc.perform(get("/game/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(game.getId()));
    }
}
