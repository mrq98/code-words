package com.mrqtech.code_words.service;

import com.mrqtech.code_words.exception.GameAlreadyFinishedException;
import com.mrqtech.code_words.exception.InvalidGameException;
import com.mrqtech.code_words.model.Game;
import com.mrqtech.code_words.repository.GameJpaRepository;
import com.mrqtech.code_words.repository.model.GameEntity;
import com.mrqtech.code_words.web.model.ForfeitRequest;
import com.mrqtech.code_words.web.model.GameRequest;
import com.mrqtech.code_words.web.model.GuessRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.mrqtech.code_words.model.Status.IN_PROGRESS;
import static com.mrqtech.code_words.model.Status.LOST;
import static com.mrqtech.code_words.model.Status.WON;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameJpaRepository gameRepository;
    private final PlayerService playerService;
    private final RandomWordGenerator randomWordGenerator;


    public Game createNewGame(GameRequest request) {
        GameEntity gameEntity = new GameEntity();
        gameEntity.setUsername(request.getUsername()); // optional -- used for leaderboards
        gameEntity.setStatus(IN_PROGRESS);
        gameEntity.setDifficulty(request.getDifficulty()); // default difficulty is easy
        gameEntity.setRemainingAttempts(request.getDifficulty().getTotalAttempts()); // increases base on difficulty

        // generate random word
        String randomWord = randomWordGenerator.getRandomWord(request.getDifficulty());
        gameEntity.setWord(randomWord);
        String maskWord = "_".repeat(randomWord.length());
        gameEntity.setMaskedWord(maskWord);

        //save to h2
        gameRepository.save(gameEntity);

        //tries to create player, if username is provided
        playerService.createPlayerIfNotExist(request.getUsername());

        // return mapped dto
        return mapToDto(gameEntity);
    }

    public Game processGuess(GuessRequest request) {

        GameEntity gameEntity = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new EntityNotFoundException("Game with id " + request.getGameId() + " not found"));


        validateGameAccess(request.getUsername(), gameEntity);

        String guess = request.getGuess();
        // should still be IN_PROGRESS
        if (gameEntity.getStatus() != IN_PROGRESS) {
            throw new GameAlreadyFinishedException("Game is already finished. status: " + gameEntity.getStatus());
        }

        gameEntity.setRemainingAttempts(gameEntity.getRemainingAttempts() - 1); // decrease remaining attempts

        if (guess.length() == 1) {   // user guessed a letter
            guessLetter(gameEntity, guess);
        } else {                     // user guessed a word
            guessWord(gameEntity, guess);
        }

        //check if attempt ran out
        if (gameEntity.getRemainingAttempts() == 0 && gameEntity.getStatus() != LOST) {
            gameEntity.setStatus(LOST);
        }

        gameEntity = gameRepository.save(gameEntity);
        return mapToDto(gameEntity);
    }

    public Game getGameById(long gameId) {
        return gameRepository.findById(gameId)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Game with id " + gameId + " not found"));
    }

    /*
     * Player guesses a letter of the word,
     * unmasks the letters that matched
     * update game status and attempt
     */
    private void guessLetter(GameEntity gameEntity, String guess) {
        // user guessed a letter, try to unmask
        String unmasked = unmaskLetter(guess.charAt(0), gameEntity.getMaskedWord(), gameEntity.getWord());
        gameEntity.setMaskedWord(unmasked); // update masked word

        // check if already matched
        if (unmasked.equals(gameEntity.getWord())) {
            gameEntity.setStatus(WON);
            playerService.updatePlayerScore(gameEntity);
        } else {
            // check if attempts ran out
            if (gameEntity.getRemainingAttempts() == 0) {
                gameEntity.setStatus(LOST);
            }
        }
    }

    /*
     * Player guesses the word,
     * unmasks all letters if matched,
     * update game status and attempt
     * updates player score
     */
    private void guessWord(GameEntity gameEntity, String guess) {
        // correct guess -- set status to WON
        if (guess.equalsIgnoreCase(gameEntity.getWord())) {
            gameEntity.setMaskedWord(gameEntity.getWord());
            gameEntity.setStatus(WON);
            playerService.updatePlayerScore(gameEntity);
        } else {

            // check if attempts ran out
            if (gameEntity.getRemainingAttempts() == 0) {
                gameEntity.setStatus(LOST);
            }
        }
    }


    // Forfeit the game by setting its status to LOST
    public Game forfeitGame(ForfeitRequest request) {
        GameEntity gameEntity = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new EntityNotFoundException("Game with id " + request.getGameId() + " not found"));


        validateGameAccess(request.getUsername(), gameEntity);

        // Only allow forfeit if game is still in progress
        if (gameEntity.getStatus() != IN_PROGRESS) {
            throw new GameAlreadyFinishedException("Cannot forfeit a game that is already finished. Status: " + gameEntity.getStatus());
        }

        gameEntity.setStatus(LOST);
        gameEntity = gameRepository.save(gameEntity);
        return mapToDto(gameEntity);
    }


    /*
     * try to unmask new letter for a correct guess
     * @param char guessedLetter
     * @param String lastMaskedWord - last state of guessed word
     * @param String word - raw/unmasked word
     *
     * @returns new masked/unmasked word
     */
    private String unmaskLetter(char guessedLetter, String lastMaskedWord, String word) {
        StringBuilder result = new StringBuilder(lastMaskedWord.length());
        guessedLetter = Character.toLowerCase(guessedLetter); // make sure we don't mind the case sensitivity

        for (int i = 0; i < word.length(); i++) {
            char originalChar = Character.toLowerCase(word.charAt(i));
            char maskedChar = lastMaskedWord.charAt(i);
            //append original if same with guessedLetter, use masked character otherwise
            result.append(originalChar == guessedLetter ? originalChar : maskedChar);
        }

        return result.toString();
    }


    // can use mapstruct lib, doing this for simplicity
    // maps GameEntity to Game dto
    // reveals word if game is already finished
    private Game mapToDto(GameEntity entity) {
        if (entity == null) return null;
        Game dto = new Game();
        dto.setId(entity.getId());
        dto.setRemainingAttempts(entity.getRemainingAttempts());
        dto.setMaskedWord(entity.getMaskedWord());
        dto.setStatus(entity.getStatus());

        if (entity.getStatus() != IN_PROGRESS) {
            dto.setWord(entity.getWord());
        }

        return dto;
    }

    // validates if game is accessible to the username or not
    private void validateGameAccess(String username, GameEntity gameEntity) {
        if (StringUtils.isNotEmpty(gameEntity.getUsername())) {
            boolean isValidUser = gameEntity.getUsername().equals(username);
            if (!isValidUser) {
                throw new InvalidGameException("Can't access this game.");
            }
        }
    }


}
