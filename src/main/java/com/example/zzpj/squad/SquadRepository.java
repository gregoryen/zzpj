package com.example.zzpj.squad;

import com.example.zzpj.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SquadRepository extends JpaRepository<Squad, Long> {

        List<Squad> getAllByGame(Game game);
      Optional<Squad> getOneById(long squadId);
    Optional<Squad> findByName(String name);
}