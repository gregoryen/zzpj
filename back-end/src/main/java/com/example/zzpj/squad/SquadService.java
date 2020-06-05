package com.example.zzpj.squad;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("squadService")
public class SquadService {

    SquadRepository squadRepository;
    GameRepository gameRepository;
    UserRepository userRepository;

    @Autowired
    public SquadService(SquadRepository squadRepository, GameRepository gameRepository, UserRepository userRepository) {
        this.squadRepository = squadRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public void createSquad(String name, String level, long gameId) {
        Game game = gameRepository.getByAppid(gameId);
        Squad squad = Squad.builder()
                .name(name)
                .level(level)
                .game(game)
                .build();
        squadRepository.save(squad);
    }

    public void assignUser(Long squadId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Squad squad = squadRepository.findById(squadId).orElseThrow();
        squad.addUser(user);
        squadRepository.save(squad);
    }
}