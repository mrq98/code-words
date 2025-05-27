package com.mrqtech.code_words.repository;


import com.mrqtech.code_words.repository.model.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameJpaRepository extends JpaRepository<GameEntity, Long> {

}
