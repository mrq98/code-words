package com.mrqtech.code_words.service;

import com.mrqtech.code_words.model.Difficulty;
import com.mrqtech.code_words.model.Player;
import com.mrqtech.code_words.model.Status;
import com.mrqtech.code_words.repository.PlayerRepository;
import com.mrqtech.code_words.repository.model.GameEntity;
import com.mrqtech.code_words.repository.model.PlayerEntity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private PlayerEntity playerEntity;
    private GameEntity gameEntity;

    @BeforeEach
    void setUp() {
        playerEntity = new PlayerEntity();
        playerEntity.setId(1L);
        playerEntity.setUsername("testuser");
        playerEntity.setTotalScore(100);
        playerEntity.setTotalEasySolved(1);
        playerEntity.setTotalNormalSolved(2);
        playerEntity.setTotalHardSolved(3);

        gameEntity = new GameEntity();
        gameEntity.setUsername("testuser");
        gameEntity.setStatus(Status.WON);
        gameEntity.setDifficulty(Difficulty.NORMAL);
        gameEntity.setRemainingAttempts(2);
    }

    @Test
    void getLeaderBoard_ReturnsTopPlayers() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<PlayerEntity> playerEntities = List.of(playerEntity);
        when(playerRepository.findTopPlayersByTotalScore(pageRequest)).thenReturn(playerEntities);

        // Act
        List<Player> result = playerService.getLeaderBoard(5);

        // Assert
        assertEquals(1, result.size());
        Player player = result.get(0);
        assertEquals(1L, player.getId());
        assertEquals("testuser", player.getUsername());
        assertEquals(100, player.getTotalScore());
        assertEquals(1, player.getTotalEasySolved());
        assertEquals(2, player.getTotalNormalSolved());
        assertEquals(3, player.getTotalHardSolved());
        verify(playerRepository).findTopPlayersByTotalScore(pageRequest);
    }

    @Test
    void getPlayerByUsername_ReturnsPlayer_WhenFound() {
        // Arrange
        when(playerRepository.findPlayerByUsername("testuser")).thenReturn(Optional.of(playerEntity));

        // Act
        Player result = playerService.getPlayerByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(100, result.getTotalScore());
        verify(playerRepository).findPlayerByUsername("testuser");
    }

    @Test
    void getPlayerByUsername_ThrowsEntityNotFoundException_WhenNotFound() {
        // Arrange
        when(playerRepository.findPlayerByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> playerService.getPlayerByUsername("unknown"));
        assertEquals("Player with username unknown not found.", exception.getMessage());
        verify(playerRepository).findPlayerByUsername("unknown");
    }

    @Test
    void createPlayer_SavesPlayer_IfNotExist_WhenUsernameIsValid() {
        // Arrange
        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(playerEntity);

        // Act
        playerService.createPlayerIfNotExist("testuser");

        // Assert
        verify(playerRepository).save(argThat(entity ->
                "testuser".equals(entity.getUsername())));
    }

    @Test
    void createPlayer_IfNotExist_DoesNotSave_WhenUsernameIsEmpty() {
        // Act
        playerService.createPlayerIfNotExist("");

        // Assert
        verify(playerRepository, never()).save(any(PlayerEntity.class));
    }

    @Test
    void updatePlayerScore_UpdatesScoreAndEasySolved_WhenGameWon() {
        // Arrange
        GameEntity gameEntity = mock(GameEntity.class); // Mock GameEntity
        Difficulty difficulty = Difficulty.EASY;
        when(gameEntity.getUsername()).thenReturn("testuser");
        when(gameEntity.getStatus()).thenReturn(Status.WON);
        when(gameEntity.getDifficulty()).thenReturn(difficulty);
        when(gameEntity.getRemainingAttempts()).thenReturn(2);
        when(playerRepository.findPlayerByUsername("testuser")).thenReturn(Optional.of(playerEntity));
        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(playerEntity);

        // Act
        playerService.updatePlayerScore(gameEntity);

        // Assert
        verify(playerRepository).save(argThat(entity ->
                entity.getTotalScore() == 103 && // 100 + (1 + 2)
                        entity.getTotalEasySolved() == 2)); // 1 + 1
    }

    @Test
    void updatePlayerScore_UpdatesScoreAndNormalSolved_WhenGameWon() {
        // Arrange
        GameEntity gameEntity = mock(GameEntity.class); // Mock GameEntity
        Difficulty difficulty = Difficulty.NORMAL; // Normal Difficulty
        when(gameEntity.getUsername()).thenReturn("testuser");
        when(gameEntity.getStatus()).thenReturn(Status.WON);
        when(gameEntity.getDifficulty()).thenReturn(difficulty);
        when(gameEntity.getRemainingAttempts()).thenReturn(2);
        when(playerRepository.findPlayerByUsername("testuser")).thenReturn(Optional.of(playerEntity));
        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(playerEntity);

        // Act
        playerService.updatePlayerScore(gameEntity);

        // Assert
        verify(playerRepository).findPlayerByUsername("testuser");
        verify(playerRepository).save(argThat(entity ->
                entity.getTotalScore() == 104 && // 100 + (2 + 2)
                        entity.getTotalNormalSolved() == 3)); // 2 + 1
    }

    @Test
    void updatePlayerScore_UpdatesScoreAndHardSolved_WhenGameWon() {
        // Arrange
        GameEntity gameEntity = mock(GameEntity.class); // Mock GameEntity
        Difficulty difficulty = Difficulty.HARD; // Hard Difficulty
        when(gameEntity.getUsername()).thenReturn("testuser");
        when(gameEntity.getStatus()).thenReturn(Status.WON);
        when(gameEntity.getDifficulty()).thenReturn(difficulty);
        when(gameEntity.getRemainingAttempts()).thenReturn(2);
        when(playerRepository.findPlayerByUsername("testuser")).thenReturn(Optional.of(playerEntity));
        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(playerEntity);

        // Act
        playerService.updatePlayerScore(gameEntity);

        // Assert
        verify(playerRepository).save(argThat(entity ->
                entity.getTotalScore() == 105 && // 100 + (3 + 2)
                        entity.getTotalHardSolved() == 4)); // 3 + 1
    }

    @Test
    void updatePlayerScore_ThrowsEntityNotFoundException_WhenPlayerNotFound() {
        // Arrange
        when(playerRepository.findPlayerByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> playerService.updatePlayerScore(gameEntity));
        assertEquals("Can't update player score. Player with username testuser not found.", exception.getMessage());
        verify(playerRepository, never()).save(any(PlayerEntity.class));
    }

    @Test
    void updatePlayerScore_DoesNotUpdate_WhenGameNotWon() {
        // Arrange
        gameEntity.setStatus(Status.LOST); // Not WON

        // Act
        playerService.updatePlayerScore(gameEntity);

        // Assert
        verify(playerRepository, never()).findPlayerByUsername(anyString());
        verify(playerRepository, never()).save(any(PlayerEntity.class));
    }

    @Test
    void updatePlayerScore_DoesNotUpdate_WhenUsernameIsEmpty() {
        // Arrange
        gameEntity.setUsername("");

        // Act
        playerService.updatePlayerScore(gameEntity);

        // Assert
        verify(playerRepository, never()).findPlayerByUsername(anyString());
        verify(playerRepository, never()).save(any(PlayerEntity.class));
    }
}