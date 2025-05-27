package com.mrqtech.code_words.service;



import com.mrqtech.code_words.exception.GameAlreadyFinishedException;
import com.mrqtech.code_words.exception.InvalidGameException;
import com.mrqtech.code_words.model.Difficulty;
import com.mrqtech.code_words.model.Game;
import com.mrqtech.code_words.model.Status;
import com.mrqtech.code_words.repository.GameJpaRepository;
import com.mrqtech.code_words.repository.model.GameEntity;
import com.mrqtech.code_words.web.model.ForfeitRequest;
import com.mrqtech.code_words.web.model.GameRequest;
import com.mrqtech.code_words.web.model.GuessRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameJpaRepository gameRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private RandomWordGenerator randomWordGenerator;

    @InjectMocks
    private GameService gameService;

    private GameEntity gameEntity;
    private GameRequest gameRequest;

    @BeforeEach
    void setUp() {
        gameRequest = new GameRequest();
        gameRequest.setUsername("testUser");
        gameRequest.setDifficulty(Difficulty.EASY);

        gameEntity = new GameEntity();
        gameEntity.setId(1L);
        gameEntity.setUsername("testUser");
        gameEntity.setStatus(Status.IN_PROGRESS);
        gameEntity.setDifficulty(Difficulty.EASY);
        gameEntity.setRemainingAttempts(Difficulty.EASY.getTotalAttempts());
        gameEntity.setWord("hello");
        gameEntity.setMaskedWord("_____");
    }

    @Test
    void createNewGame_success() {
        when(randomWordGenerator.getRandomWord(Difficulty.EASY)).thenReturn("hello");
        when(gameRepository.save(any(GameEntity.class))).thenReturn(gameEntity);
        doNothing().when(playerService).createPlayerIfNotExist("testUser");

        Game result = gameService.createNewGame(gameRequest);

        assertNotNull(result);
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        assertEquals("testUser", gameEntity.getUsername());
        assertEquals("_ _ _ _ _", result.getMaskedWord());
        assertEquals(Difficulty.EASY.getTotalAttempts(), result.getRemainingAttempts());
        verify(gameRepository).save(any(GameEntity.class));
        verify(playerService).createPlayerIfNotExist("testUser");
    }

    @Test
    void processGuess_letterGuess_correctLetter() {
        gameEntity.setRemainingAttempts(6);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));
        when(gameRepository.save(any(GameEntity.class))).thenReturn(gameEntity);


        Game result = gameService.processGuess(new GuessRequest(1L, "h", gameEntity.getUsername()));

        assertEquals(5, result.getRemainingAttempts());
        assertEquals("h _ _ _ _", result.getMaskedWord());
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        verify(gameRepository).save(gameEntity);
        verify(playerService, never()).updatePlayerScore(any());
    }

    @Test
    void processGuess_letterGuess_winGame() {
        gameEntity.setWord("hi");
        gameEntity.setMaskedWord("h _");
        gameEntity.setRemainingAttempts(6);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));
        when(gameRepository.save(any(GameEntity.class))).thenReturn(gameEntity);

        Game result = gameService.processGuess(new GuessRequest(1L, "i", gameEntity.getUsername()));

        assertEquals("h i", result.getMaskedWord());
        assertEquals(Status.WON, result.getStatus());
        assertEquals("hi", result.getWord());
        verify(playerService).updatePlayerScore(gameEntity);
    }

    @Test
    void processGuess_letterGuess_loseGame() {
        gameEntity.setRemainingAttempts(1);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));
        when(gameRepository.save(any(GameEntity.class))).thenReturn(gameEntity);

        Game result = gameService.processGuess(new GuessRequest(1L, "x", gameEntity.getUsername()));

        assertEquals(0, result.getRemainingAttempts());
        assertEquals(Status.LOST, result.getStatus());
        assertEquals("hello", result.getWord());
        verify(playerService, never()).updatePlayerScore(any());
    }

    @Test
    void processGuess_wordGuess_correctWord() {
        gameEntity.setRemainingAttempts(6);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));
        when(gameRepository.save(any(GameEntity.class))).thenReturn(gameEntity);

        Game result = gameService.processGuess(new GuessRequest(1L, "hello", gameEntity.getUsername()));

        assertEquals("h e l l o", result.getMaskedWord());
        assertEquals(Status.WON, result.getStatus());
        assertEquals("hello", result.getWord());
        verify(playerService).updatePlayerScore(gameEntity);
    }

    @Test
    void processGuess_wordGuess_incorrectWord_loseGame() {
        gameEntity.setRemainingAttempts(1);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));
        when(gameRepository.save(any(GameEntity.class))).thenReturn(gameEntity);

        Game result = gameService.processGuess(new GuessRequest(1L, "wrong", gameEntity.getUsername()));

        assertEquals(0, result.getRemainingAttempts());
        assertEquals(Status.LOST, result.getStatus());
        assertEquals("hello", result.getWord());
        verify(playerService, never()).updatePlayerScore(any());
    }

    @Test
    void processGuess_gameNotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> gameService.processGuess(new GuessRequest(1L, "h", gameEntity.getUsername())));

        assertEquals("Game with id 1 not found", exception.getMessage());
    }

    @Test
    void processGuess_gameAlreadyFinished() {
        gameEntity.setStatus(Status.WON);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));

        GameAlreadyFinishedException exception = assertThrows(GameAlreadyFinishedException.class,
                () -> gameService.processGuess(new GuessRequest(1L, "h", gameEntity.getUsername())));

        assertEquals("Game is already finished. status: WON", exception.getMessage());
    }

    @Test
    void processGuess_invalidGameAccess() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));

        InvalidGameException exception = assertThrows(InvalidGameException.class,
                () -> gameService.processGuess(new GuessRequest(1L, "h", "nonGameCreatorUsername")));

        assertEquals("Can't access this game.", exception.getMessage());
    }


    @Test
    void getGameById_success() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));

        Game result = gameService.getGameById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("_ _ _ _ _", result.getMaskedWord());
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        assertNull(result.getWord());
    }

    @Test
    void getGameById_notFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> gameService.getGameById(1L));

        assertEquals("Game with id 1 not found", exception.getMessage());
    }

    @Test
    void forfeitGame_success() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));
        when(gameRepository.save(any(GameEntity.class))).thenReturn(gameEntity);

        Game result = gameService.forfeitGame(new ForfeitRequest(1L,gameEntity.getUsername()));

        assertEquals(Status.LOST, result.getStatus());
        assertEquals("hello", result.getWord());
        verify(gameRepository).save(gameEntity);
    }

    @Test
    void forfeitGame_alreadyFinished() {
        gameEntity.setStatus(Status.WON);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));

        GameAlreadyFinishedException exception = assertThrows(GameAlreadyFinishedException.class,
                () -> gameService.forfeitGame(new ForfeitRequest(1L,gameEntity.getUsername())));

        assertEquals("Cannot forfeit a game that is already finished. Status: WON", exception.getMessage());
    }

    @Test
    void forfeitGame_invalidGameAccess() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gameEntity));

        InvalidGameException exception = assertThrows(InvalidGameException.class,
                () -> gameService.forfeitGame(new ForfeitRequest(1L,"nonGameCreatorUsername")));

        assertEquals("Can't access this game.", exception.getMessage());
    }

    @Test
    void forfeitGame_notFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> gameService.forfeitGame(new ForfeitRequest(1L,gameEntity.getUsername())));

        assertEquals("Game with id 1 not found", exception.getMessage());
    }
}