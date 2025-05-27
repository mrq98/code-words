package com.mrqtech.code_words.service;


import com.mrqtech.code_words.model.Difficulty;
import com.mrqtech.code_words.model.Player;
import com.mrqtech.code_words.model.Status;
import com.mrqtech.code_words.repository.PlayerRepository;
import com.mrqtech.code_words.repository.model.GameEntity;
import com.mrqtech.code_words.repository.model.PlayerEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;


    public List<Player> getLeaderBoard(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit); // page 0, size = limit
        return playerRepository.findTopPlayersByTotalScore(pageRequest)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Player getPlayerByUsername(String username) {
        return playerRepository.findPlayerByUsername(username)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Player with username " + username + " not found."));

    }


    //create a player
    public void createPlayer(String username) {
        if (StringUtils.isNotEmpty(username)) {
            PlayerEntity playerEntity = new PlayerEntity();
            playerEntity.setUsername(username);
            playerRepository.save(playerEntity);
        }
    }

    // update player score if game has username and status is WON
    public void updatePlayerScore(GameEntity gameEntity) {
        if (StringUtils.isNotEmpty(gameEntity.getUsername()) && gameEntity.getStatus() == Status.WON) {
            PlayerEntity player = playerRepository.findPlayerByUsername(gameEntity.getUsername())
                    .map(p -> {
                        int updatedScore = p.getTotalScore() + getPlayerScore(gameEntity);
                        p.setTotalScore(updatedScore);
                        updateTotalSolved(p, gameEntity.getDifficulty());
                        return p;
                    })
                    .orElseThrow(() -> new EntityNotFoundException("Can't update player score. Player with username " + gameEntity.getUsername() + " not found."));

            playerRepository.save(player);
        }
    }

    private void updateTotalSolved(PlayerEntity player, Difficulty difficulty) {
        switch (difficulty) {
            case NORMAL:
                player.setTotalNormalSolved(player.getTotalNormalSolved() + 1);
                break;
            case HARD:
                player.setTotalHardSolved(player.getTotalHardSolved() + 1);
                break;
            default:
                player.setTotalEasySolved(player.getTotalEasySolved() + 1);
                break;
        }
    }

    private int getPlayerScore(GameEntity gameEntity) {
        return gameEntity.getDifficulty().getBaseScore() + gameEntity.getRemainingAttempts();
    }

    private Player mapToDto(PlayerEntity playerEntity) {
        Player player = new Player();
        player.setId(playerEntity.getId());
        player.setUsername(playerEntity.getUsername());
        player.setTotalScore(playerEntity.getTotalScore());
        player.setTotalEasySolved(playerEntity.getTotalEasySolved());
        player.setTotalNormalSolved(playerEntity.getTotalNormalSolved());
        player.setTotalHardSolved(playerEntity.getTotalHardSolved());
        return player;
    }


}
