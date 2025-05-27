package com.mrqtech.code_words.repository;

import com.mrqtech.code_words.repository.model.PlayerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;


    @Test
    void findTopPlayersByTotalScore_ReturnsPlayersSortedByScore_WithPagination() {
        // Arrange
        PlayerEntity player1 = new PlayerEntity();
        player1.setUsername("user1");
        player1.setTotalScore(150);
        player1.setTotalEasySolved(1);
        player1.setTotalNormalSolved(2);
        player1.setTotalHardSolved(3);
        playerRepository.save(player1);

        PlayerEntity player2 = new PlayerEntity();
        player2.setUsername("user2");
        player2.setTotalScore(200);
        player2.setTotalEasySolved(2);
        player2.setTotalNormalSolved(3);
        player2.setTotalHardSolved(4);
        playerRepository.save(player2);

        PlayerEntity player3 = new PlayerEntity();
        player3.setUsername("user3");
        player3.setTotalScore(100);
        player3.setTotalEasySolved(0);
        player3.setTotalNormalSolved(1);
        player3.setTotalHardSolved(2);
        playerRepository.save(player3);


        Pageable pageable = PageRequest.of(0, 2); // First page, 2 items

        // Act
        List<PlayerEntity> topPlayers = playerRepository.findTopPlayersByTotalScore(pageable);

        // Assert
        assertEquals(2, topPlayers.size(), "Should return 2 players");
        assertEquals("user2", topPlayers.get(0).getUsername(), "First player should have highest score (200)");
        assertEquals(200, topPlayers.get(0).getTotalScore(), "First player score should be 200");
        assertEquals("user1", topPlayers.get(1).getUsername(), "Second player should have second-highest score (150)");
        assertEquals(150, topPlayers.get(1).getTotalScore(), "Second player score should be 150");
    }

    @Test
    void findPlayerByUsername_ReturnsPlayer_WhenUsernameExists() {
        // Arrange
        PlayerEntity player = new PlayerEntity();
        player.setUsername("testuser");
        player.setTotalScore(100);
        player.setTotalEasySolved(1);
        player.setTotalNormalSolved(2);
        player.setTotalHardSolved(3);
        playerRepository.save(player);

        // Act
        Optional<PlayerEntity> foundPlayer = playerRepository.findPlayerByUsername("testuser");

        // Assert
        assertTrue(foundPlayer.isPresent(), "Player should be found");
        assertEquals("testuser", foundPlayer.get().getUsername(), "Username should match");
        assertEquals(100, foundPlayer.get().getTotalScore(), "Total score should match");
        assertEquals(1, foundPlayer.get().getTotalEasySolved(), "Easy solved should match");
        assertEquals(2, foundPlayer.get().getTotalNormalSolved(), "Normal solved should match");
        assertEquals(3, foundPlayer.get().getTotalHardSolved(), "Hard solved should match");
    }

    @Test
    void findPlayerByUsername_ReturnsEmpty_WhenUsernameDoesNotExist() {
        // Act
        Optional<PlayerEntity> foundPlayer = playerRepository.findPlayerByUsername("nonexistent");

        // Assert
        assertFalse(foundPlayer.isPresent(), "Player should not be found");
    }
}
