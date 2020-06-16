package com.example.zzpj.squad;

import com.example.zzpj.security.UserService;

import com.example.zzpj.steam_api.SteamApi;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity createSquad(@RequestParam String name, @RequestParam String level, @RequestParam long gameId) {
        try {
            squadService.createSquad(name, level, gameId, SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    @PutMapping(path = "/assign", consumes = "application/json")
    public ResponseEntity assignUser(@RequestParam Long squadId, @RequestParam Long userId) {

        try {
            squadService.assignUser(squadId, userId, SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<JSONObject>> getAllSquads() {
        try {
            return ResponseEntity.ok().body(squadService.getAllSquads());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/bySquadId")
    public ResponseEntity<List<JSONObject>> getUsersBySquadId(@RequestParam long squadId) {
        try {
            return ResponseEntity.ok().body(squadService.getUsersBySquadId(squadId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/all/{userId}")
    public List<Squad> getAllSquadsForUser(@PathVariable long userId) {
        return squadService.getAllSquadsForUser(userId);
    }

    @GetMapping(path = "/squadInfo/{squadId}")
    public Squad getSquadInfo(@PathVariable long squadId) {
        return squadService.getSquadInfoBySquadId(squadId);
    }
}