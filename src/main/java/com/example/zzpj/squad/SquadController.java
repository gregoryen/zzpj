package com.example.zzpj.squad;

import com.example.zzpj.security.UserService;

import com.example.zzpj.steam_api.SteamApi;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("squad")
public class SquadController {

    AuthenticationManager authenticationManager;
    SquadService squadService;
    SteamApi steamApi;
    UserService userService;

    @Autowired
    public SquadController(SquadService squadService, SteamApi steamApi, UserService userService) {
        this.squadService = squadService;
        this.steamApi = steamApi;
        this.userService = userService;
    }

    @PostMapping(path = "/create", consumes = "application/json")
    public void createSquad(@RequestParam String name, @RequestParam String level, @RequestParam long gameId) {
        squadService.createSquad(name, level, gameId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PutMapping(path = "/assign", consumes = "application/json")
    public void assignUser(@RequestParam Long squadId, @RequestParam Long userId) {
        squadService.assignUser(squadId, userId,SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping(path = "/all")
    public List<JSONObject> getAllSquads() {
        return squadService.getAllSquads();
    }

    @GetMapping(path = "/bySquadId")
    public List<JSONObject> getUsersBySquadId(@RequestParam long squadId) {
        return squadService.getUsersBySquadId(squadId);
    }



}