package com.example.zzpj.game;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game getByAppid(Long appId);

    List<Game> findAll();
}
