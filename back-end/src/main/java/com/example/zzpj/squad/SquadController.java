package com.example.zzpj.squad;

import com.example.zzpj.game.GameController;
import com.example.zzpj.security.UserService;
import com.example.zzpj.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("squad")
public class SquadController {

    SquadService squadService;
    GameService gameService;
    UserService userService;

    @Autowired
    public SquadController(SquadService squadService, GameService gameService, UserService userService) {
        this.squadService = squadService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @PostMapping(path = "/create", consumes = "application/json")
    public void createSquad(@RequestParam String name, @RequestParam String level, @RequestParam long gameId) {
        squadService.createSquad(name, level, gameId);
    }

    @PutMapping(path = "/assign", consumes = "application/json")
    public void assignUser(@RequestParam Long squadId, @RequestParam Long userId) {
        squadService.assignUser(squadId, userId);
    }
}