package com.example.zzpj.squad;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.ranking.Rate;
import com.example.zzpj.ranking.RateRepository;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("squadService")
public class SquadService {

    SquadRepository squadRepository;
    GameRepository gameRepository;
    UserRepository userRepository;

    @Autowired
    public SquadService(SquadRepository squadRepository, GameRepository gameRepository,
                        UserRepository userRepository) {
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

    public List<JSONObject> getAllSquads() {
        return parseSquadsToJSONObjects(squadRepository.findAll());
    }

    public List<JSONObject> getUsersBySquadId(final long squadId) {
        Optional<Squad> opt = squadRepository.findById(squadId);
        List<JSONObject> entities = new ArrayList<>();
        if (opt.isPresent()) {
            List<User> users = opt.get().getUsers();
            for (User u : users) {
                JSONObject entity = new JSONObject();
                entity.put("id: ", u.getId());
                entity.put("login: ", u.getLogin());
                entities.add(entity);
            }
        }
        return entities;
    }

    private List<JSONObject> parseSquadsToJSONObjects(List<Squad> squads) {
        List<JSONObject> entities = new ArrayList<>();
        for (int i = 0; i < squads.size(); i++) {
            JSONObject entity = new JSONObject();
            entity.put("id: ", squads.get(i).getId());
            entity.put("name: ", squads.get(i).getName());
            entity.put("level: ", squads.get(i).getLevel());
            entity.put("game: ", squads.get(i).getGame().getName());
            entities.add(entity);
        }
        return entities;
    }
}