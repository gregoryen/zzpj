package com.example.zzpj.squad;

import com.example.zzpj.exception.ApiRequestException;
import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;

import com.example.zzpj.squad.exceptions.SquadNotExistException;

import com.example.zzpj.ranking.Rate;
import com.example.zzpj.ranking.RateRepository;

import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.support.NullValue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


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
        Game game = gameRepository.findByAppid(gameId)
                .orElseThrow(() -> new ApiRequestException("Such game does not exist."));
        squadRepository.findByName(name)
                .ifPresent(s -> {
                    throw new ApiRequestException("Squad with this name already exists.");
                });
        String owner = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        Squad squad = Squad.builder()
                .owner(owner)
                .name(name)
                .level(level)
                .game(game)
                .users(new ArrayList<User>())
                .build();
        squad.addUser(userRepository.getByLogin(owner));
        squadRepository.save(squad);
    }

    public void assignUser(Long squadId, Long userId) {
        String name = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new ApiRequestException("Squad does not exist."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User to assign does not exist."));
        if (squad.getUsers().contains(user))
            throw new ApiRequestException("User is already assigned.");

        if (userRepository.existsByLogin(name)) {
            if (squad.getOwner().equals(name)) {
                squad.addUser(user);
                squadRepository.save(squad);
            } else {
                throw new ApiRequestException("You are not an owner of this squad, you can't assign user to it.");
            }
        }
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
        for (Squad squad : squads) {
            JSONObject entity = new JSONObject();
            entity.put("owner: ", squad.getOwner());
            entity.put("id: ", squad.getId());
            entity.put("name: ", squad.getName());
            entity.put("level: ", squad.getLevel());
            entity.put("game: ", squad.getGame().getName());
            entities.add(entity);
        }
        return entities;

    }
    public void removeSquad(long squadId){

        if(squadRepository.getOneById(squadId).isPresent())
        squadRepository.deleteById(squadId);
        else
            throw new SquadNotExistException("Squad with " + squadId + " id not exist");
    }
}