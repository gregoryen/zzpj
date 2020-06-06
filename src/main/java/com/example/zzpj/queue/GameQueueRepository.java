package com.example.zzpj.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameQueueRepository extends JpaRepository<GameQueue, Long> {

    Optional<GameQueue> findByGameName(String gameName);
    void deleteGameQueueByGameName(String gameName);
    List<GameQueue> findAll();

}
