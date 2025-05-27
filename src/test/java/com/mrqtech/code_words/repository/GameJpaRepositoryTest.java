package com.mrqtech.code_words.repository;

import com.mrqtech.code_words.model.Difficulty;
import com.mrqtech.code_words.model.Status;
import com.mrqtech.code_words.repository.model.GameEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GameJpaRepositoryTest {

    @Autowired
    private GameJpaRepository gameJpaRepository;

    @Test
    void save_PersistsGameEntity_Successfully() {
        // Arrange
        GameEntity gameEntity = new GameEntity();
        gameEntity.setUsername("testuser");
        gameEntity.setStatus(Status.WON);
        gameEntity.setDifficulty(Difficulty.EASY);
        gameEntity.setRemainingAttempts(2);
        gameEntity.setMaskedWord("______");
        gameEntity.setWord("banana");
        // Act
        GameEntity savedEntity = gameJpaRepository.save(gameEntity);

        // Assert
        assertNotNull(savedEntity, "Saved entity should not be null");
        assertEquals("testuser", savedEntity.getUsername(), "Username should match");
        assertEquals(Status.WON, savedEntity.getStatus(), "Status should match");
        assertEquals(Difficulty.EASY, savedEntity.getDifficulty(), "Difficulty should match");
        assertEquals("______", savedEntity.getMaskedWord(), "Masked word should match");
        assertEquals("banana", savedEntity.getWord(), "Word should match");
        assertEquals(2, savedEntity.getRemainingAttempts(), "Remaining attempts should match");
    }

    @Test
    void findById_ReturnsGameEntity_WhenEntityExists() {
        // Arrange
        GameEntity gameEntity = new GameEntity();
        gameEntity.setUsername("testuser");
        gameEntity.setStatus(Status.WON);
        gameEntity.setDifficulty(Difficulty.EASY);
        gameEntity.setRemainingAttempts(2);
        gameEntity.setMaskedWord("______");
        gameEntity.setWord("banana");

        gameJpaRepository.save(gameEntity);


        // Act
        Optional<GameEntity> foundEntity = gameJpaRepository.findById(2L);

        // Assert
        assertTrue(foundEntity.isPresent(), "Entity should be found");
        assertEquals("testuser", foundEntity.get().getUsername(), "Username should match");
        assertEquals(Status.WON, foundEntity.get().getStatus(), "Status should match");
        assertEquals(Difficulty.EASY, foundEntity.get().getDifficulty(), "Difficulty should match");
        assertEquals("______", foundEntity.get().getMaskedWord(), "Masked word should match");
        assertEquals("banana", foundEntity.get().getWord(), "Word should match");
        assertEquals(2, foundEntity.get().getRemainingAttempts(), "Remaining attempts should match");
    }

    @Test
    void findById_ReturnsEmpty_WhenEntityDoesNotExist() {
        // Act
        Optional<GameEntity> foundEntity = gameJpaRepository.findById(3L);

        // Assert
        assertFalse(foundEntity.isPresent(), "Entity should not be found");
    }



}