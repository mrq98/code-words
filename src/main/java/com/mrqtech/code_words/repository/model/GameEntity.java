package com.mrqtech.code_words.repository.model;

import com.mrqtech.code_words.model.Difficulty;
import com.mrqtech.code_words.model.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "masked_word", nullable = false)
    private String maskedWord;

    @Column(name = "remaining_attempts", nullable = false)
    private Integer remainingAttempts;

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(name = "username", nullable = true)
    private String username;

    @Column(name = "is_multiplayer", nullable = false)
    private Boolean isMultiplayer = false;
}
