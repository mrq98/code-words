package com.mrqtech.code_words.repository;

import com.mrqtech.code_words.repository.model.PlayerEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    @Query("SELECT p FROM PlayerEntity p ORDER BY p.totalScore DESC")
    List<PlayerEntity> findTopPlayersByTotalScore(Pageable pageable);

    Optional<PlayerEntity> findPlayerByUsername(String username);

}
