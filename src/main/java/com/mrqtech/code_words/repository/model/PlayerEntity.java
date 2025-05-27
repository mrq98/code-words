package com.mrqtech.code_words.repository.model;

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
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "total_score")
    private Integer totalScore = 0;

    @Column(name = "total_easy_solved")
    private Integer totalEasySolved = 0;

    @Column(name = "total_normal_solved")
    private Integer totalNormalSolved = 0;

    @Column(name = "total_hard_solved")
    private Integer totalHardSolved = 0;
}
