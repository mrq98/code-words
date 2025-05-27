package com.mrqtech.code_words.web;

import com.mrqtech.code_words.model.Player;
import com.mrqtech.code_words.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @Test
    void getPlayer_WhenPlayerExists_ReturnsPlayerAndOkStatus() throws Exception {
        // Arrange
        String username = "testUser";
        Player player = createPlayer(username, 1000);

        given(playerService.getPlayerByUsername(username)).willReturn(player);

        // Act & Assert
        mockMvc.perform(get("/players")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.totalScore").value(1000));
    }

    @Test
    void getLeaderBoard_WhenLimitProvided_ReturnsPlayersAndOkStatus() throws Exception {
        // Arrange
        int limit = 2;
        List<Player> players = List.of(
                createPlayer("player1", 1500),
                createPlayer("player2", 1200)
        );
        given(playerService.getLeaderBoard(limit)).willReturn(players);

        // Act & Assert
        mockMvc.perform(get("/players/leaderboard")
                        .param("limit", String.valueOf(limit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("player1"))
                .andExpect(jsonPath("$[0].totalScore").value(1500))
                .andExpect(jsonPath("$[1].username").value("player2"))
                .andExpect(jsonPath("$[1].totalScore").value(1200));
    }

    private Player createPlayer(String username, int score) {
        Player player = new Player();
        player.setUsername(username);
        player.setTotalScore(score);
        return player;
    }
}