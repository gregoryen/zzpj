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


import java.util.Collection;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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


    public void createSquad(String name, String level, long gameId, String login) {
        Game game = gameRepository.findByAppid(gameId)
                .orElseThrow(() -> new ApiRequestException("Such game does not exist."));
        squadRepository.findByName(name)
                .ifPresent(s -> {
                    throw new ApiRequestException("Squad with this name already exists.");
                });
        String owner = login;
        Squad squad = Squad.builder()
                .owner(owner)
                .name(name)
                .level(level)
                .game(game)
                .users(new ArrayList<User>())
                .build();
        squad.addUser(userRepository.getByLogin(owner));
        System.out.println(squad.getId());
        squadRepository.save(squad);
    }

    public void assignUser(Long squadId, Long userId, String ownerLogin) {

        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new ApiRequestException("Squad does not exist."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User to assign does not exist."));
        if (squad.getUsers().contains(user))
            throw new ApiRequestException("User is already assigned.");

        if (userRepository.existsByLogin(ownerLogin)) {
            if (squad.getOwner().equals(ownerLogin)) {
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
    Optional<Squad> optionalSquad= squadRepository.findById(squadId);
        if(optionalSquad.isPresent()) {
            Squad squad = optionalSquad.get();
            for(User user : squad.getUsers()){
                user.getSquads().remove(squad);
                userRepository.save(user);
            }
            squad.setUsers(new ArrayList<>());
            squadRepository.save(squad);
            squadRepository.deleteById(squadId);
        }
        else
            throw new SquadNotExistException("Squad with " + squadId + " id not exist");
    }


    public List<Squad> getAllSquadsForUser(long userId) {
        List<Squad> squads = squadRepository.findAll();
        List<Squad> result = new ArrayList<>();
        for (Squad s : squads) {
            List<User> users = s.getUsers();
            boolean isPresent = users.stream()
                    .anyMatch(u -> u.getId() == userId);
            if (isPresent)
                result.add(s);
        }
        return result;
    }

    public Squad getSquadInfoBySquadId(long squadId) {
        return squadRepository.findById(squadId)
                .orElseThrow(() -> new ApiRequestException("Squad does not exist."));
    }

}