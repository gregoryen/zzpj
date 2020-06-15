package com.example.zzpj.squad;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.squad.exceptions.SquadNotExistException;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Squad createSquad(String name, String level, long gameId) {
        Game game = gameRepository.getByAppid(gameId);
        Squad squad = Squad.builder()
                .name(name)
                .level(level)
                .game(game)
                .build();
        return squadRepository.save(squad);
    }

    public void assignUser(Long squadId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Squad squad = squadRepository.findById(squadId).orElseThrow();
        squad.addUser(user);
        squadRepository.save(squad);
    }
    public void removeSquad(long squadId){

        if(squadRepository.getOneById(squadId).isPresent())
        squadRepository.deleteById(squadId);
        else
            throw new SquadNotExistException("Squad with " + squadId + " id not exist");
    }
}